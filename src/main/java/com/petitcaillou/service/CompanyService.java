package com.petitcaillou.service;

import java.util.List;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyInUseException;
import com.petitcaillou.domain.company.exceptions.CompanyNameAlreadyUsedException;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;

public class CompanyService
{
  private final CompanyRepository companies;
  private final JobApplicationRepository applications;

  public CompanyService(CompanyRepository companies, JobApplicationRepository applications)
  {
    this.companies = companies;
    this.applications = applications;
  }

  public Company create(String name, String website)
  {
    Company company = Company.create(name, website);
    requireUniqueName(company.name(), null);
    return companies.save(company);
  }

  public Company update(CompanyId id, String name, String website)
  {
    byId(id);

    Company updated = Company.reconstitute(id, name, website);
    requireUniqueName(updated.name(), id);
    return companies.save(updated);
  }

  public void delete(CompanyId id)
  {
    byId(id);

    if (applications.existsByCompany(id))
    {
      throw new CompanyInUseException();
    }

    companies.delete(id);
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

  private void requireUniqueName(String name, CompanyId allowedId)
  {
    companies.findByName(name).ifPresent(existing ->
    {
      if (!existing.id().equals(allowedId))
      {
        throw new CompanyNameAlreadyUsedException(existing.id());
      }
    });
  }
}
