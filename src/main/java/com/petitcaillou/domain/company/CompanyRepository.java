package com.petitcaillou.domain.company;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;

public interface CompanyRepository
{
  Company save(Company company);

  Optional<Company> findById(CompanyId id);

  Optional<Company> findByNormalizedName(NormalizedName normalizedName);

  List<Company> findAllByIds(Collection<CompanyId> ids);

  boolean existsById(CompanyId id);

  Page<Company> search(String normalizedPrefix, PageRequest pageRequest);

  void delete(CompanyId id);
}
