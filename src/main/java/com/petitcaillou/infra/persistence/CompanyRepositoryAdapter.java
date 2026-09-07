package com.petitcaillou.infra.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.NormalizedName;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;

@Component
public class CompanyRepositoryAdapter implements CompanyRepository
{
  private static final Sort BY_NAME = Sort.by("normalizedName").and(Sort.by("id"));

  private final SpringDataCompanyRepository jpa;

  public CompanyRepositoryAdapter(SpringDataCompanyRepository jpa)
  {
    this.jpa = jpa;
  }

  @Override
  public Company save(Company company)
  {
    return CompanyMapper.toDomain(jpa.save(CompanyMapper.toEntity(company)));
  }

  @Override
  public Optional<Company> findById(CompanyId id)
  {
    return jpa.findById(id.value().toString()).map(CompanyMapper::toDomain);
  }

  @Override
  public Optional<Company> findByNormalizedName(NormalizedName normalizedName)
  {
    return jpa.findByNormalizedName(normalizedName.value()).map(CompanyMapper::toDomain);
  }

  @Override
  public List<Company> findAllByIds(Collection<CompanyId> ids)
  {
    return jpa.findAllById(ids.stream().map(id -> id.value().toString()).toList())
      .stream().map(CompanyMapper::toDomain).toList();
  }

  @Override
  public boolean existsById(CompanyId id)
  {
    return jpa.existsById(id.value().toString());
  }

  @Override
  public Page<Company> search(String normalizedPrefix, PageRequest pageRequest)
  {
    var page = jpa.findByNormalizedNameStartingWith(
      normalizedPrefix, PageMapper.toPageable(pageRequest.page(), pageRequest.size(), BY_NAME));
    return PageMapper.toDomain(
      page.getContent().stream().map(CompanyMapper::toDomain).toList(),
      pageRequest.page(),
      pageRequest.size(),
      page.getTotalElements());
  }

  @Override
  public void delete(CompanyId id)
  {
    jpa.deleteById(id.value().toString());
  }
}
