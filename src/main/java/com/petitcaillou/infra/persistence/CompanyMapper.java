package com.petitcaillou.infra.persistence;

import java.util.UUID;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;

final class CompanyMapper
{
  private CompanyMapper()
  {
  }

  static CompanyEntity toEntity(Company company)
  {
    return new CompanyEntity(company.id().value().toString(), company.name(), company.website());
  }

  static Company toDomain(CompanyEntity entity)
  {
    return Company.reconstitute(CompanyId.of(UUID.fromString(entity.getId())), entity.getName(), entity.getWebsite());
  }
}
