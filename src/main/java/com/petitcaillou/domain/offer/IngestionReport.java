package com.petitcaillou.domain.offer;

import java.util.List;

public record IngestionReport(int created, int updated, int unchanged, List<Failure> failures, String nextCursor)
{
  public IngestionReport
  {
    failures = List.copyOf(failures);
  }

  public record Failure(ScannedOffer offer, String reason)
  {
  }
}
