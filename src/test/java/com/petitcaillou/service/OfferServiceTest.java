package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.SystemAccount;
import com.petitcaillou.domain.offer.exceptions.OfferNotFoundException;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OfferServiceTest
{
  private static final Username USER = Username.of("alice");
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final OfferId ID = OfferId.of(UUID.randomUUID());
  private static final OfferDetails DETAILS = new OfferDetails(
    COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "A role");

  private final OfferRepository offers = mock(OfferRepository.class);
  private final CompanyRepository companies = mock(CompanyRepository.class);
  private final OfferService service = new OfferService(offers, companies);

  @Test
  void given_knownCompanyAndNoDuplicate_when_creatingPrivate_then_saves()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(offers.findByHashAndCreatedBy(any(), any())).thenReturn(Optional.empty());
    when(offers.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.createPrivate(USER, DETAILS);

    verify(offers).save(any(Offer.class));
  }

  @Test
  void given_unknownCompany_when_creatingPrivate_then_throwsCompanyNotFound()
  {
    when(companies.existsById(COMPANY)).thenReturn(false);

    assertThatThrownBy(() -> service.createPrivate(USER, DETAILS))
      .isInstanceOf(CompanyNotFoundException.class);
  }

  @Test
  void given_duplicateHashInScope_when_creating_then_returnsExistingWithoutSaving()
  {
    Offer existing = Offer.reconstitute(ID, USER, DETAILS, true);
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(offers.findByHashAndCreatedBy(any(), any())).thenReturn(Optional.of(existing));

    Offer result = service.createPrivate(USER, DETAILS);

    assertThat(result.id()).isEqualTo(ID);
  }

  @Test
  void given_duplicateHashInScope_when_creating_then_neverSaves()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(offers.findByHashAndCreatedBy(any(), any())).thenReturn(Optional.of(Offer.reconstitute(ID, USER, DETAILS, true)));

    service.createPrivate(USER, DETAILS);

    verify(offers, never()).save(any());
  }

  @Test
  void given_ingestion_when_persisting_then_offerIsPublic()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(offers.findByHashAndCreatedBy(any(), any())).thenReturn(Optional.empty());
    when(offers.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Offer ingested = service.ingest(DETAILS, false);

    assertThat(ingested.createdBy()).isEqualTo(SystemAccount.USERNAME);
  }

  @Test
  void given_visibleOffer_when_gettingById_then_returnsIt()
  {
    when(offers.findById(ID)).thenReturn(Optional.of(Offer.reconstitute(ID, USER, DETAILS, true)));

    assertThat(service.byId(USER, ID).id()).isEqualTo(ID);
  }

  @Test
  void given_privateOfferOfAnother_when_gettingById_then_throwsNotFound()
  {
    when(offers.findById(ID)).thenReturn(Optional.of(Offer.reconstitute(ID, Username.of("bob"), DETAILS, true)));

    assertThatThrownBy(() -> service.byId(USER, ID))
      .isInstanceOf(OfferNotFoundException.class);
  }

  @Test
  void given_missingOffer_when_gettingById_then_throwsNotFound()
  {
    when(offers.findById(ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.byId(USER, ID))
      .isInstanceOf(OfferNotFoundException.class);
  }

  @Test
  void given_user_when_listing_then_delegatesToRepository()
  {
    PageRequest pageRequest = new PageRequest(0, 20);
    Page<Offer> page = new Page<>(List.of(Offer.reconstitute(ID, USER, DETAILS, true)), 0, 20, 1);
    when(offers.findVisibleTo(USER, pageRequest)).thenReturn(page);

    assertThat(service.list(USER, pageRequest).totalElements()).isEqualTo(1);
  }
}
