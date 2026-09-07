package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.CatalogWriter;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SourcePage;
import com.petitcaillou.domain.offer.SystemAccount;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferIngestionServiceTest
{
  private static final LocalDate SINCE = LocalDate.of(2026, 1, 1);
  private static final LocalDate UNTIL = LocalDate.of(2026, 1, 31);
  private static final ScannedOffer SCANNED = new ScannedOffer(
    "Acme", "https://acme.example", "Backend Engineer", "Paris", SINCE,
    "https://jobs.example/1", "role", "board", "1");

  private final OfferSource source = mock(OfferSource.class);
  private final CatalogWriter catalog = mock(CatalogWriter.class);
  private final OfferIngestionService service = new OfferIngestionService(source, catalog);

  private Offer sampleOffer()
  {
    OfferDetails details = new OfferDetails(CompanyId.of(UUID.randomUUID()), "Backend Engineer", "Paris", SINCE, null, null);
    return Offer.ingested(details, false, "board", "1");
  }

  private void outcome(OfferWriteResult.Outcome outcome)
  {
    when(catalog.ingest(any())).thenReturn(new OfferWriteResult(sampleOffer(), outcome));
  }

  @Test
  void given_newOffer_when_ingesting_then_countsCreated()
  {
    when(source.fetch(SINCE, UNTIL, null)).thenReturn(new SourcePage(List.of(SCANNED), null));
    outcome(OfferWriteResult.Outcome.CREATED);

    assertThat(service.ingestWindow(SINCE, UNTIL).created()).isEqualTo(1);
  }

  @Test
  void given_enrichedOffer_when_ingesting_then_countsUpdated()
  {
    when(source.fetch(SINCE, UNTIL, null)).thenReturn(new SourcePage(List.of(SCANNED), null));
    outcome(OfferWriteResult.Outcome.UPDATED);

    assertThat(service.ingestWindow(SINCE, UNTIL).updated()).isEqualTo(1);
  }

  @Test
  void given_alreadyKnownOffer_when_ingesting_then_countsUnchanged()
  {
    when(source.fetch(SINCE, UNTIL, null)).thenReturn(new SourcePage(List.of(SCANNED), null));
    outcome(OfferWriteResult.Outcome.UNCHANGED);

    assertThat(service.ingestWindow(SINCE, UNTIL).unchanged()).isEqualTo(1);
  }

  @Test
  void given_failingEntry_when_ingesting_then_retainsItForReplay()
  {
    when(source.fetch(SINCE, UNTIL, null)).thenReturn(new SourcePage(List.of(SCANNED), null));
    when(catalog.ingest(any())).thenThrow(new IllegalStateException("Unavailable"));

    assertThat(service.ingestWindow(SINCE, UNTIL).failures().getFirst().offer()).isEqualTo(SCANNED);
  }

  @Test
  void given_sourceCursor_when_ingestingPage_then_returnsResumeCursor()
  {
    when(source.fetch(SINCE, UNTIL, "first")).thenReturn(new SourcePage(List.of(), "second"));

    assertThat(service.ingestPage(SINCE, UNTIL, "first").nextCursor()).isEqualTo("second");
  }

  @Test
  void given_stuckCursor_when_ingesting_then_rejectsSourcePage()
  {
    when(source.fetch(SINCE, UNTIL, "first")).thenReturn(new SourcePage(List.of(), "first"));

    assertThatThrownBy(() -> service.ingestPage(SINCE, UNTIL, "first")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_reversedWindow_when_ingesting_then_rejectsIt()
  {
    assertThatThrownBy(() -> service.ingestWindow(UNTIL, SINCE)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_systemAccount_when_ingesting_then_offersAreOwnedByIt()
  {
    assertThat(sampleOffer().createdBy()).isEqualTo(SystemAccount.USERNAME);
  }
}
