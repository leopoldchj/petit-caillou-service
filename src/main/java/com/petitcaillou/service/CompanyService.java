package com.petitcaillou.service;

import java.util.List;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.NormalizedName;
import com.petitcaillou.domain.company.exceptions.CompanyInUseException;
import com.petitcaillou.domain.company.exceptions.CompanyNameAlreadyUsedException;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.offer.OfferRepository;

public class CompanyService
{
  private final CompanyRepository companies;
  private final OfferRepository offers;

  public CompanyService(CompanyRepository companies, OfferRepository offers)
  {
    this.companies = companies;
    this.offers = offers;
  }

  public Company create(String name, String website)
  {
    Company company = Company.create(name, website);
    requireUniqueName(company.normalizedName(), null);
    return companies.save(company);
  }

  public Company resolveOrCreate(String name, String website)
  {
    Company candidate = Company.create(name, website);
    return companies.findByNormalizedName(candidate.normalizedName())
      .orElseGet(() -> companies.save(candidate));
  }

  public Company update(CompanyId id, String name, String website)
  {
    byId(id);

    Company updated = Company.reconstitute(id, name, website);
    requireUniqueName(updated.normalizedName(), id);
    return companies.save(updated);
  }

  public void delete(CompanyId id)
  {
    byId(id);

    if (offers.existsByCompany(id))
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

  private void requireUniqueName(NormalizedName normalizedName, CompanyId allowedId)
  {
    companies.findByNormalizedName(normalizedName).ifPresent(existing ->
    {
      if (!existing.id().equals(allowedId))
      {
        throw new CompanyNameAlreadyUsedException(existing.id());
      }
    });
  }
}
