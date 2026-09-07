package com.petitcaillou.service;

import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.offer.ContentHash;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.SystemAccount;
import com.petitcaillou.domain.offer.exceptions.OfferNotFoundException;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.company.CompanyRepository;

public class OfferService
{
  private final OfferRepository offers;
  private final CompanyRepository companies;

  public OfferService(OfferRepository offers, CompanyRepository companies)
  {
    this.offers = offers;
    this.companies = companies;
  }

  public Offer createPrivate(Username owner, OfferDetails details)
  {
    return persist(owner, details, true);
  }

  public Offer ingest(OfferDetails details, boolean verified)
  {
    return persist(SystemAccount.USERNAME, details, verified);
  }

  public Offer byId(Username user, OfferId id)
  {
    Offer offer = offers.findById(id).orElseThrow(OfferNotFoundException::new);
    if (!offer.isVisibleTo(user))
    {
      throw new OfferNotFoundException();
    }
    return offer;
  }

  public Page<Offer> list(Username user, PageRequest pageRequest)
  {
    return offers.findVisibleTo(user, pageRequest);
  }

  private Offer persist(Username createdBy, OfferDetails details, boolean verified)
  {
    requireCompany(details);
    Offer offer = Offer.create(createdBy, details, verified);
    ContentHash hash = offer.contentHash();
    return offers.findByHashAndCreatedBy(hash, createdBy).orElseGet(() -> offers.save(offer));
  }

  private void requireCompany(OfferDetails details)
  {
    if (details.companyId() == null || !companies.existsById(details.companyId()))
    {
      throw new CompanyNotFoundException();
    }
  }
}
