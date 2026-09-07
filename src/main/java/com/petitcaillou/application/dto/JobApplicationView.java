package com.petitcaillou.application.dto;

import java.time.LocalDate;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.offer.Offer;

public record JobApplicationView(
  String id,
  OfferView offer,
  LocalDate applicationDate,
  String responseStatus,
  String notes)
{
  public static JobApplicationView from(JobApplication application, Offer offer, Company company)
  {
    return new JobApplicationView(
      application.id().value().toString(),
      OfferView.from(offer, company),
      application.applicationDate(),
      application.responseStatus().name(),
      application.notes());
  }
}
