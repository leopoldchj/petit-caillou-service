package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.ContentHash;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferRepositoryAdapterTest
{
  private static final UUID ID = UUID.randomUUID();
  private static final UUID COMPANY = UUID.randomUUID();

  private final SpringDataOfferRepository jpa = mock(SpringDataOfferRepository.class);
  private final OfferRepositoryAdapter adapter = new OfferRepositoryAdapter(jpa);

  private OfferEntity storedEntity()
  {
    return new OfferEntity(ID.toString(), COMPANY.toString(), "Backend Engineer", "Paris",
      LocalDate.of(2026, 8, 30), "https://x/1", "role", "alice", true, "key");
  }

  private Offer domainOffer()
  {
    OfferDetails details = new OfferDetails(CompanyId.of(COMPANY), "Backend Engineer", "Paris",
      LocalDate.of(2026, 8, 30), "https://x/1", "role");
    return Offer.reconstitute(OfferId.of(ID), Username.of("alice"), details, true);
  }

  @Test
  void given_offer_when_saving_then_returnsMappedSavedOffer()
  {
    when(jpa.save(any(OfferEntity.class))).thenReturn(storedEntity());

    assertThat(adapter.save(domainOffer()).id().value()).isEqualTo(ID);
  }

  @Test
  void given_storedEntity_when_findingById_then_returnsMappedOffer()
  {
    when(jpa.findById(ID.toString())).thenReturn(Optional.of(storedEntity()));

    assertThat(adapter.findById(OfferId.of(ID)).orElseThrow().title()).isEqualTo("Backend Engineer");
  }

  @Test
  void given_ids_when_findingAllByIds_then_returnsMappedOffers()
  {
    when(jpa.findAllById(anyCollection())).thenReturn(List.of(storedEntity()));

    assertThat(adapter.findAllByIds(List.of(OfferId.of(ID)))).hasSize(1);
  }

  @Test
  void given_dedupKeyAndScope_when_lookingUp_then_delegatesToJpa()
  {
    when(jpa.findByContentHashAndCreatedBy("key", "alice")).thenReturn(Optional.of(storedEntity()));

    assertThat(adapter.findByDedupKeyAndCreatedBy(ContentHash.ofValue("key"), Username.of("alice"))).isPresent();
  }

  @Test
  void given_company_when_checkingUsage_then_delegatesToJpa()
  {
    when(jpa.existsByCompanyId(COMPANY.toString())).thenReturn(true);

    assertThat(adapter.existsByCompany(CompanyId.of(COMPANY))).isTrue();
  }

  @Test
  void given_visibleOffers_when_listing_then_returnsMappedPage()
  {
    when(jpa.findByCreatedByIn(anyCollection(), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(storedEntity())));

    assertThat(adapter.findVisibleTo(Username.of("alice"), new PageRequest(0, 20)).items()).hasSize(1);
  }

  @Test
  void given_id_when_checkingExistence_then_delegatesToJpa()
  {
    when(jpa.existsById(ID.toString())).thenReturn(true);

    assertThat(adapter.existsById(OfferId.of(ID))).isTrue();
  }
}
