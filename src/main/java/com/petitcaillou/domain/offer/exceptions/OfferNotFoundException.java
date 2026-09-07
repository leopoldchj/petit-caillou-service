package com.petitcaillou.domain.offer.exceptions;

public class OfferNotFoundException extends RuntimeException
{
  public OfferNotFoundException()
  {
    super("Offer does not exist");
  }
}
