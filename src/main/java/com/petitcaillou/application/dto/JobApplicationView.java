package com.petitcaillou.application.dto;

import java.time.LocalDate;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.jobapplication.JobApplication;

public record JobApplicationView(
  String id,
  CompanyView company,
  String link,
  String title,
  String description,
  LocalDate applicationDate,
  String responseStatus)
{
  public static JobApplicationView from(JobApplication application, Company company)
  {
    return new JobApplicationView(
      application.id().value().toString(),
      CompanyView.from(company),
      application.link(),
      application.title(),
      application.description(),
      application.applicationDate(),
      application.responseStatus().name());
  }
}
