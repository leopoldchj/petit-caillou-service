package com.petitcaillou.application.dto;

import java.time.LocalDate;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.offer.Offer;

public record OfferView(
  String id,
  CompanyView company,
  String title,
  String location,
  LocalDate publicationDate,
  String link,
  String description,
  boolean verified,
  String visibility)
{
  public static OfferView from(Offer offer, Company company)
  {
    return new OfferView(
      offer.id().value().toString(),
      CompanyView.from(company),
      offer.title(),
      offer.location(),
      offer.publicationDate(),
      offer.link() == null ? null : offer.link().value(),
      offer.description(),
      offer.verified(),
      offer.visibility().name());
  }
}
