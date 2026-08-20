package com.petitcaillou.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;

@Component
public class CompanyRepositoryAdapter implements CompanyRepository
{
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
  public Optional<Company> findByName(String name)
  {
    return jpa.findByName(name).map(CompanyMapper::toDomain);
  }

  @Override
  public boolean existsById(CompanyId id)
  {
    return jpa.existsById(id.value().toString());
  }

  @Override
  public List<Company> findAll()
  {
    return jpa.findAll().stream().map(CompanyMapper::toDomain).toList();
  }

  @Override
  public void delete(CompanyId id)
  {
    jpa.deleteById(id.value().toString());
  }
}
