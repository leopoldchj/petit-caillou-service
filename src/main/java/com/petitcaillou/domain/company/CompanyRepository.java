package com.petitcaillou.domain.company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository
{
  Company save(Company company);

  Optional<Company> findById(CompanyId id);

  Optional<Company> findByNormalizedName(NormalizedName normalizedName);

  boolean existsById(CompanyId id);

  List<Company> findAll();

  void delete(CompanyId id);
}
