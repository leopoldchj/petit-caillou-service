package com.petitcaillou.domain.offer;

import java.time.LocalDate;

public record ScannedOffer(
  String companyName,
  String companyWebsite,
  String title,
  String location,
  LocalDate publicationDate,
  String link,
  String description,
  String source,
  String externalId)
{
}
