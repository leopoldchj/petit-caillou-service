package com.petitcaillou.domain.jobapplication;

import java.time.LocalDate;

import com.petitcaillou.domain.offer.OfferId;

public record JobApplicationDetails(
  OfferId offerId,
  LocalDate applicationDate,
  ResponseStatus responseStatus,
  String notes)
{
}
