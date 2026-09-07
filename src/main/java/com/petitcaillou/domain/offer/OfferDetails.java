package com.petitcaillou.domain.offer;

import java.time.LocalDate;

import com.petitcaillou.domain.company.CompanyId;

public record OfferDetails(
  CompanyId companyId,
  String title,
  String location,
  LocalDate publicationDate,
  String link,
  String description)
{
}
