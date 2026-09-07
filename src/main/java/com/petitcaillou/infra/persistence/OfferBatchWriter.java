package com.petitcaillou.infra.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SystemAccount;

@Component
class OfferBatchWriter
{
  private final CompanyResolver companies;
  private final SpringDataOfferRepository offers;

  OfferBatchWriter(CompanyResolver companies, SpringDataOfferRepository offers)
  {
    this.companies = companies;
    this.offers = offers;
  }

  List<OfferWriteResult> write(List<ScannedOffer> batch)
  {
    List<OfferWriteResult> results = new ArrayList<>();
    List<Draft> drafts = draft(batch, results);
    Map<String, CompanyId> companyIds = companies.resolve(drafts.stream().map(Draft::company).toList());
    List<Candidate> candidates = prepare(drafts, companyIds, results);

    Map<String, OfferEntity> known = load(candidates);
    List<OfferEntity> inserts = new ArrayList<>();
    List<OfferEntity> updates = new ArrayList<>();

    for (Candidate candidate : candidates)
    {
      OfferEntity stored = known.get(candidate.offer().dedupKey().value());
      if (stored == null)
      {
        inserts.add(OfferMapper.toNewEntity(candidate.offer()));
        results.add(OfferWriteResult.of(candidate.source(), OfferWriteResult.Outcome.CREATED));
        continue;
      }

      OfferMapper.toDomain(stored).updatedWith(candidate.offer().details()).ifPresentOrElse(
        merged ->
        {
          updates.add(OfferMapper.toEntity(merged));
          results.add(OfferWriteResult.of(candidate.source(), OfferWriteResult.Outcome.UPDATED));
        },
        () -> results.add(OfferWriteResult.of(candidate.source(), OfferWriteResult.Outcome.UNCHANGED)));
    }

    offers.saveAll(inserts);
    offers.saveAll(updates);
    return results;
  }

  private List<Draft> draft(List<ScannedOffer> batch, List<OfferWriteResult> results)
  {
    List<Draft> drafts = new ArrayList<>();
    for (ScannedOffer item : batch)
    {
      try
      {
        drafts.add(new Draft(item, Company.create(item.companyName(), item.companyWebsite())));
      }
      catch (RuntimeException invalid)
      {
        results.add(OfferWriteResult.failed(item, reason(invalid)));
      }
    }
    return drafts;
  }

  private List<Candidate> prepare(List<Draft> drafts, Map<String, CompanyId> companyIds,
    List<OfferWriteResult> results)
  {
    List<Candidate> candidates = new ArrayList<>();
    Set<String> seen = new HashSet<>();

    for (Draft draft : drafts)
    {
      ScannedOffer item = draft.source();
      try
      {
        OfferDetails details = new OfferDetails(companyIds.get(draft.company().normalizedName().value()),
          item.title(), item.location(), item.publicationDate(), item.link(), item.description());
        Offer offer = Offer.ingested(details, false, item.source(), item.externalId());

        if (seen.add(offer.dedupKey().value()))
        {
          candidates.add(new Candidate(item, offer));
        }
        else
        {
          results.add(OfferWriteResult.of(item, OfferWriteResult.Outcome.UNCHANGED));
        }
      }
      catch (RuntimeException invalid)
      {
        results.add(OfferWriteResult.failed(item, reason(invalid)));
      }
    }
    return candidates;
  }

  private Map<String, OfferEntity> load(List<Candidate> candidates)
  {
    if (candidates.isEmpty())
    {
      return Map.of();
    }

    Set<String> keys = new HashSet<>();
    candidates.forEach(candidate -> keys.add(candidate.offer().dedupKey().value()));

    Map<String, OfferEntity> byKey = new HashMap<>();
    offers.findByCreatedByAndContentHashIn(SystemAccount.USERNAME.value(), keys)
      .forEach(entity -> byKey.put(entity.getContentHash(), entity));
    return byKey;
  }

  private static String reason(RuntimeException exception)
  {
    return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
  }

  private record Draft(ScannedOffer source, Company company)
  {
  }

  private record Candidate(ScannedOffer source, Offer offer)
  {
  }
}
