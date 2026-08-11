package com.petitcaillou.domain.company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository
{
  Company save(Company company);

  Optional<Company> findById(CompanyId id);

  Optional<Company> findByName(String name);

  boolean existsById(CompanyId id);

  List<Company> findAll();
}
