package com.petitcaillou.infra.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.NormalizedName;
import com.petitcaillou.domain.offer.CatalogWriter;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SystemAccount;

@Component
public class CatalogWriterAdapter implements CatalogWriter
{
  private final CompanyRepository companies;
  private final OfferRepository offers;

  public CatalogWriterAdapter(CompanyRepository companies, OfferRepository offers)
  {
    this.companies = companies;
    this.offers = offers;
  }

  @Override
  @Transactional
  public OfferWriteResult ingest(ScannedOffer scanned)
  {
    Company company = resolveCompany(scanned.companyName(), scanned.companyWebsite());
    OfferDetails details = new OfferDetails(company.id(), scanned.title(), scanned.location(),
      scanned.publicationDate(), scanned.link(), scanned.description());
    Offer incoming = Offer.ingested(details, false, scanned.source(), scanned.externalId());

    return offers.findByDedupKeyAndCreatedBy(incoming.dedupKey(), SystemAccount.USERNAME)
      .map(existing -> enrich(existing, incoming))
      .orElseGet(() -> new OfferWriteResult(offers.save(incoming), OfferWriteResult.Outcome.CREATED));
  }

  private OfferWriteResult enrich(Offer existing, Offer incoming)
  {
    Offer merged = existing.enrichedWith(incoming.details());
    if (existing.hasSameContent(merged))
    {
      return new OfferWriteResult(existing, OfferWriteResult.Outcome.UNCHANGED);
    }
    return new OfferWriteResult(offers.save(merged), OfferWriteResult.Outcome.UPDATED);
  }

  private Company resolveCompany(String name, String website)
  {
    return companies.findByNormalizedName(NormalizedName.of(name))
      .orElseGet(() -> companies.save(Company.create(name, website)));
  }
}
