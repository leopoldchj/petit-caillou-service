package com.petitcaillou.service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.exceptions.OfferNotFoundException;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

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
    if (details.companyId() == null || !companies.existsById(details.companyId()))
    {
      throw new CompanyNotFoundException();
    }
    Offer offer = Offer.create(owner, details, true);
    return offers.findByDedupKeyAndCreatedBy(offer.dedupKey(), owner).orElseGet(() -> offers.save(offer));
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

  public Map<OfferId, Offer> byIds(Collection<OfferId> ids)
  {
    return offers.findAllByIds(ids).stream().collect(Collectors.toMap(Offer::id, Function.identity()));
  }

  public Page<Offer> list(Username user, PageRequest pageRequest)
  {
    return offers.findVisibleTo(user, pageRequest);
  }
}
