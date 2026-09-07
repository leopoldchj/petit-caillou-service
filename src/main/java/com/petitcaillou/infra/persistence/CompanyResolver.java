package com.petitcaillou.infra.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;

@Component
class CompanyResolver
{
  private final SpringDataCompanyRepository companies;

  CompanyResolver(SpringDataCompanyRepository companies)
  {
    this.companies = companies;
  }

  Map<String, CompanyId> resolve(List<Company> candidates)
  {
    if (candidates.isEmpty())
    {
      return Map.of();
    }

    Set<String> keys = new HashSet<>();
    candidates.forEach(candidate -> keys.add(candidate.normalizedName().value()));

    Map<String, CompanyId> byKey = new HashMap<>();
    companies.findByNormalizedNameIn(keys)
      .forEach(entity -> byKey.put(entity.getNormalizedName(), CompanyMapper.toDomain(entity).id()));

    List<CompanyEntity> missing = new ArrayList<>();
    for (Company candidate : candidates)
    {
      if (byKey.putIfAbsent(candidate.normalizedName().value(), candidate.id()) == null)
      {
        missing.add(CompanyMapper.toNewEntity(candidate));
      }
    }
    companies.saveAll(missing);
    return byKey;
  }
}
