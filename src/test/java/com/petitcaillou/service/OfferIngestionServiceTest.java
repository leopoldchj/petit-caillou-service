package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.ScannedOffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OfferIngestionServiceTest
{
  private static final LocalDate SINCE = LocalDate.of(2026, 1, 1);
  private static final LocalDate UNTIL = LocalDate.of(2026, 1, 31);
  private static final ScannedOffer SCANNED = new ScannedOffer(
    "Acme", "https://acme.example", "Backend Engineer", "Paris", LocalDate.of(2026, 1, 15), "https://x/1", "role");

  private final OfferSource source = mock(OfferSource.class);
  private final CompanyService companies = mock(CompanyService.class);
  private final OfferService offers = mock(OfferService.class);
  private final OfferIngestionService service = new OfferIngestionService(source, companies, offers);

  @Test
  void given_scannedOffers_when_ingesting_then_resolvesCompanyAndIngestsEach()
  {
    when(source.fetch(SINCE, UNTIL)).thenReturn(List.of(SCANNED));
    when(companies.resolveOrCreate(any(), any())).thenReturn(Company.reconstitute(CompanyId.of(UUID.randomUUID()),
      "Acme", "https://acme.example"));

    service.ingestWindow(SINCE, UNTIL);

    verify(offers).ingest(any(), anyBoolean());
  }

  @Test
  void given_scannedOffers_when_ingesting_then_returnsTheCount()
  {
    when(source.fetch(SINCE, UNTIL)).thenReturn(List.of(SCANNED, SCANNED));
    when(companies.resolveOrCreate(any(), any())).thenReturn(Company.reconstitute(CompanyId.of(UUID.randomUUID()),
      "Acme", null));

    assertThat(service.ingestWindow(SINCE, UNTIL)).isEqualTo(2);
  }
}
