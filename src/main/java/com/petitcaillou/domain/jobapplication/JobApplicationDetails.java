package com.petitcaillou.domain.jobapplication;

import java.time.LocalDate;

import com.petitcaillou.domain.company.CompanyId;

public record JobApplicationDetails(
  String link,
  CompanyId companyId,
  String title,
  String description,
  String location,
  LocalDate applicationDate,
  ResponseStatus responseStatus)
{
}
