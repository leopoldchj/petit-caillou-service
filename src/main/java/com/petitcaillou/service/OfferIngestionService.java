package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.petitcaillou.domain.offer.CatalogWriter;
import com.petitcaillou.domain.offer.IngestionReport;
import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SourcePage;

public class OfferIngestionService
{
  private final OfferSource source;
  private final CatalogWriter catalog;

  public OfferIngestionService(OfferSource source, CatalogWriter catalog)
  {
    this.source = source;
    this.catalog = catalog;
  }

  public IngestionReport ingestWindow(LocalDate since, LocalDate until)
  {
    return ingestPage(since, until, null);
  }

  public IngestionReport ingestPage(LocalDate since, LocalDate until, String cursor)
  {
    if (since == null || until == null || since.isAfter(until))
    {
      throw new IllegalArgumentException("An ordered, bounded date window is required");
    }

    SourcePage page = source.fetch(since, until, cursor);
    if (page.nextCursor() != null && page.nextCursor().equals(cursor))
    {
      throw new IllegalArgumentException("Source cursor did not advance");
    }

    int created = 0;
    int updated = 0;
    int unchanged = 0;
    List<IngestionReport.Failure> failures = new ArrayList<>();

    for (ScannedOffer scanned : page.items())
    {
      try
      {
        switch (catalog.ingest(scanned).outcome())
        {
          case CREATED -> created++;
          case UPDATED -> updated++;
          case UNCHANGED -> unchanged++;
        }
      }
      catch (RuntimeException exception)
      {
        String reason = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        failures.add(new IngestionReport.Failure(scanned, reason));
      }
    }

    return new IngestionReport(created, updated, unchanged, failures, page.nextCursor());
  }
}
