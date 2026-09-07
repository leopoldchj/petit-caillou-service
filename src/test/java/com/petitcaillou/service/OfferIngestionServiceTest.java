package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.offer.CatalogWriter;
import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SourcePage;

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

  private void sourceReturnsOneOffer()
  {
    when(source.fetch(SINCE, UNTIL, null)).thenReturn(new SourcePage(List.of(SCANNED), null));
  }

  private void catalogReports(OfferWriteResult.Outcome outcome)
  {
    when(catalog.ingestAll(any())).thenReturn(List.of(OfferWriteResult.of(SCANNED, outcome)));
  }

  @Test
  void given_newOffer_when_ingesting_then_countsCreated()
  {
    sourceReturnsOneOffer();
    catalogReports(OfferWriteResult.Outcome.CREATED);

    assertThat(service.ingestWindow(SINCE, UNTIL).created()).isEqualTo(1);
  }

  @Test
  void given_enrichedOffer_when_ingesting_then_countsUpdated()
  {
    sourceReturnsOneOffer();
    catalogReports(OfferWriteResult.Outcome.UPDATED);

    assertThat(service.ingestWindow(SINCE, UNTIL).updated()).isEqualTo(1);
  }

  @Test
  void given_alreadyKnownOffer_when_ingesting_then_countsUnchanged()
  {
    sourceReturnsOneOffer();
    catalogReports(OfferWriteResult.Outcome.UNCHANGED);

    assertThat(service.ingestWindow(SINCE, UNTIL).unchanged()).isEqualTo(1);
  }

  @Test
  void given_rejectedEntry_when_ingesting_then_retainsItForReplay()
  {
    sourceReturnsOneOffer();
    when(catalog.ingestAll(any())).thenReturn(List.of(OfferWriteResult.failed(SCANNED, "Unavailable")));

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
  void given_missingWindow_when_ingesting_then_rejectsIt()
  {
    assertThatThrownBy(() -> service.ingestWindow(null, UNTIL)).isInstanceOf(IllegalArgumentException.class);
  }
}
