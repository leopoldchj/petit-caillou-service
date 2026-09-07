package com.petitcaillou.domain.offer;

public record OfferWriteResult(Offer offer, Outcome outcome)
{
  public enum Outcome
  {
    CREATED,
    UPDATED,
    UNCHANGED
  }
}
