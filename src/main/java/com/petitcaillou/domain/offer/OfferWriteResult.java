package com.petitcaillou.domain.offer;

public record OfferWriteResult(ScannedOffer source, Outcome outcome, String reason)
{
  public enum Outcome
  {
    CREATED,
    UPDATED,
    UNCHANGED,
    FAILED
  }

  public static OfferWriteResult of(ScannedOffer source, Outcome outcome)
  {
    return new OfferWriteResult(source, outcome, null);
  }

  public static OfferWriteResult failed(ScannedOffer source, String reason)
  {
    return new OfferWriteResult(source, Outcome.FAILED, reason);
  }
}
