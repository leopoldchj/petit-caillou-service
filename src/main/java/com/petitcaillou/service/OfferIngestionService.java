package com.petitcaillou.service;

import java.time.LocalDate;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.ScannedOffer;

public class OfferIngestionService
{
  private final OfferSource source;
  private final CompanyService companies;
  private final OfferService offers;

  public OfferIngestionService(OfferSource source, CompanyService companies, OfferService offers)
  {
    this.source = source;
    this.companies = companies;
    this.offers = offers;
  }

  public int ingestWindow(LocalDate since, LocalDate until)
  {
    int ingested = 0;
    for (ScannedOffer scanned : source.fetch(since, until))
    {
      Company company = companies.resolveOrCreate(scanned.companyName(), scanned.companyWebsite());
      OfferDetails details = new OfferDetails(
        company.id(),
        scanned.title(),
        scanned.location(),
        scanned.publicationDate(),
        scanned.link(),
        scanned.description());
      offers.ingest(details, true);
      ingested++;
    }
    return ingested;
  }
}
