package com.petitcaillou.application.dto;

import com.petitcaillou.domain.company.Company;

public record CompanyView(
  String id,
  String name,
  String website)
{
  public static CompanyView from(Company company)
  {
    return new CompanyView(
      company.id().value().toString(),
      company.name(),
      company.website() == null ? null : company.website().value());
  }
}
