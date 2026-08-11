package com.petitcaillou.service;

import java.util.List;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyNameAlreadyUsedException;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;

public class CompanyService
{
  private final CompanyRepository companies;

  public CompanyService(CompanyRepository companies)
  {
    this.companies = companies;
  }

  public Company create(String name, String website)
  {
    Company company = Company.create(name, website);

    companies.findByName(company.name()).ifPresent(existing ->
    {
      throw new CompanyNameAlreadyUsedException(existing.id());
    });

    return companies.save(company);
  }

  public Company byId(CompanyId id)
  {
    return companies.findById(id)
      .orElseThrow(CompanyNotFoundException::new);
  }

  public List<Company> all()
  {
    return companies.findAll();
  }
}
