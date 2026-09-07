package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.NormalizedName;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SystemAccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogWriterAdapterTest
{
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final ScannedOffer SCANNED = new ScannedOffer(
    "Acme", "https://acme.example", "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30),
    "https://x/1", "role", "board", "42");

  private final CompanyRepository companies = mock(CompanyRepository.class);
  private final OfferRepository offers = mock(OfferRepository.class);
  private final CatalogWriterAdapter adapter = new CatalogWriterAdapter(companies, offers);

  private Company company()
  {
    return Company.reconstitute(COMPANY, "Acme", "https://acme.example");
  }

  private Offer existing()
  {
    OfferDetails details = new OfferDetails(COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30),
      "https://x/1", "role");
    return Offer.ingested(details, false, "board", "42");
  }

  @Test
  void given_unknownCompany_when_ingesting_then_createsIt()
  {
    when(companies.findByNormalizedName(any(NormalizedName.class))).thenReturn(Optional.empty());
    when(companies.save(any())).thenReturn(company());
    when(offers.findByDedupKeyAndCreatedBy(any(), any())).thenReturn(Optional.empty());
    when(offers.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    adapter.ingest(SCANNED);

    verify(companies).save(any(Company.class));
  }

  @Test
  void given_newOffer_when_ingesting_then_createsIt()
  {
    when(companies.findByNormalizedName(any(NormalizedName.class))).thenReturn(Optional.of(company()));
    when(offers.findByDedupKeyAndCreatedBy(any(), any())).thenReturn(Optional.empty());
    when(offers.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    assertThat(adapter.ingest(SCANNED).outcome()).isEqualTo(OfferWriteResult.Outcome.CREATED);
  }

  @Test
  void given_sameOfferAgain_when_ingesting_then_isUnchanged()
  {
    when(companies.findByNormalizedName(any(NormalizedName.class))).thenReturn(Optional.of(company()));
    when(offers.findByDedupKeyAndCreatedBy(any(), any())).thenReturn(Optional.of(existing()));

    assertThat(adapter.ingest(SCANNED).outcome()).isEqualTo(OfferWriteResult.Outcome.UNCHANGED);
  }

  @Test
  void given_sameOfferAgain_when_ingesting_then_neverSaves()
  {
    when(companies.findByNormalizedName(any(NormalizedName.class))).thenReturn(Optional.of(company()));
    when(offers.findByDedupKeyAndCreatedBy(any(), any())).thenReturn(Optional.of(existing()));

    adapter.ingest(SCANNED);

    verify(offers, never()).save(any());
  }

  @Test
  void given_changedOffer_when_ingesting_then_isUpdated()
  {
    when(companies.findByNormalizedName(any(NormalizedName.class))).thenReturn(Optional.of(company()));
    when(offers.findByDedupKeyAndCreatedBy(any(), any())).thenReturn(Optional.of(existing()));
    when(offers.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    ScannedOffer changed = new ScannedOffer("Acme", "https://acme.example", "Senior Backend Engineer", "Paris",
      LocalDate.of(2026, 8, 30), "https://x/1", "role", "board", "42");

    assertThat(adapter.ingest(changed).outcome()).isEqualTo(OfferWriteResult.Outcome.UPDATED);
  }

  @Test
  void given_ingestedOffer_when_ingesting_then_offerIsOwnedBySystem()
  {
    when(companies.findByNormalizedName(any(NormalizedName.class))).thenReturn(Optional.of(company()));
    when(offers.findByDedupKeyAndCreatedBy(any(), any())).thenReturn(Optional.empty());
    when(offers.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    assertThat(adapter.ingest(SCANNED).offer().createdBy()).isEqualTo(SystemAccount.USERNAME);
  }
}
