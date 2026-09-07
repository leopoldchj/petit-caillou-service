package com.petitcaillou.infra.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.NormalizedName;
import com.petitcaillou.domain.offer.CatalogWriter;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SystemAccount;

@Component
public class CatalogWriterAdapter implements CatalogWriter
{
  static final int CHUNK_SIZE = 50;

  private static final String SYSTEM = SystemAccount.USERNAME.value();

  private final SpringDataCompanyRepository companies;
  private final SpringDataOfferRepository offers;
  private final TransactionTemplate transactions;

  public CatalogWriterAdapter(SpringDataCompanyRepository companies, SpringDataOfferRepository offers,
    TransactionTemplate transactions)
  {
    this.companies = companies;
    this.offers = offers;
    this.transactions = transactions;
  }

  @Override
  public List<OfferWriteResult> ingestAll(List<ScannedOffer> scanned)
  {
    List<OfferWriteResult> results = new ArrayList<>();
    for (int start = 0; start < scanned.size(); start += CHUNK_SIZE)
    {
      List<ScannedOffer> chunk = scanned.subList(start, Math.min(start + CHUNK_SIZE, scanned.size()));
      results.addAll(writeChunkOrIsolate(chunk));
    }
    return results;
  }

  private List<OfferWriteResult> writeChunkOrIsolate(List<ScannedOffer> chunk)
  {
    try
    {
      return inTransaction(chunk);
    }
    catch (RuntimeException chunkFailure)
    {
      return isolate(chunk);
    }
  }

  private List<OfferWriteResult> isolate(List<ScannedOffer> chunk)
  {
    List<OfferWriteResult> results = new ArrayList<>();
    for (ScannedOffer item : chunk)
    {
      try
      {
        results.addAll(inTransaction(List.of(item)));
      }
      catch (RuntimeException failure)
      {
        results.add(OfferWriteResult.failed(item, reason(failure)));
      }
    }
    return results;
  }

  private List<OfferWriteResult> inTransaction(List<ScannedOffer> chunk)
  {
    List<OfferWriteResult> written = transactions.execute(status -> writeChunk(chunk));
    return written == null ? List.of() : written;
  }

  private List<OfferWriteResult> writeChunk(List<ScannedOffer> chunk)
  {
    List<OfferWriteResult> results = new ArrayList<>();
    Map<ScannedOffer, NormalizedName> named = normalize(chunk, results);
    Map<String, CompanyId> byCompanyKey = resolveCompanies(named);
    List<Candidate> candidates = prepare(named, byCompanyKey, results);
    Map<String, OfferEntity> existing = loadExisting(candidates);

    List<OfferEntity> inserts = new ArrayList<>();
    List<OfferEntity> updates = new ArrayList<>();

    for (Candidate candidate : candidates)
    {
      OfferEntity stored = existing.get(candidate.offer().dedupKey().value());
      if (stored == null)
      {
        inserts.add(OfferMapper.toNewEntity(candidate.offer()));
        results.add(OfferWriteResult.of(candidate.source(), OfferWriteResult.Outcome.CREATED));
        continue;
      }

      Offer current = OfferMapper.toDomain(stored);
      Offer merged = current.enrichedWith(candidate.offer().details());
      if (current.hasSameContent(merged))
      {
        results.add(OfferWriteResult.of(candidate.source(), OfferWriteResult.Outcome.UNCHANGED));
        continue;
      }

      updates.add(OfferMapper.toEntity(merged));
      results.add(OfferWriteResult.of(candidate.source(), OfferWriteResult.Outcome.UPDATED));
    }

    offers.saveAll(inserts);
    offers.saveAll(updates);
    return results;
  }

  private Map<ScannedOffer, NormalizedName> normalize(List<ScannedOffer> chunk, List<OfferWriteResult> results)
  {
    Map<ScannedOffer, NormalizedName> named = new LinkedHashMap<>();
    for (ScannedOffer item : chunk)
    {
      try
      {
        named.put(item, NormalizedName.of(item.companyName()));
      }
      catch (RuntimeException invalid)
      {
        results.add(OfferWriteResult.failed(item, reason(invalid)));
      }
    }
    return named;
  }

  private Map<String, CompanyId> resolveCompanies(Map<ScannedOffer, NormalizedName> named)
  {
    Set<String> keys = new HashSet<>();
    named.values().forEach(name -> keys.add(name.value()));
    if (keys.isEmpty())
    {
      return Map.of();
    }

    Map<String, CompanyId> byKey = new HashMap<>();
    companies.findByNormalizedNameIn(keys)
      .forEach(entity -> byKey.put(entity.getNormalizedName(), CompanyMapper.toDomain(entity).id()));

    List<CompanyEntity> missing = new ArrayList<>();
    for (Map.Entry<ScannedOffer, NormalizedName> entry : named.entrySet())
    {
      String key = entry.getValue().value();
      if (byKey.containsKey(key))
      {
        continue;
      }
      Company created = Company.create(entry.getKey().companyName(), entry.getKey().companyWebsite());
      byKey.put(key, created.id());
      missing.add(CompanyMapper.toNewEntity(created));
    }
    companies.saveAll(missing);
    return byKey;
  }

  private List<Candidate> prepare(Map<ScannedOffer, NormalizedName> named, Map<String, CompanyId> byCompanyKey,
    List<OfferWriteResult> results)
  {
    List<Candidate> candidates = new ArrayList<>();
    Set<String> seen = new HashSet<>();

    for (Map.Entry<ScannedOffer, NormalizedName> entry : named.entrySet())
    {
      ScannedOffer item = entry.getKey();
      try
      {
        CompanyId companyId = byCompanyKey.get(entry.getValue().value());
        OfferDetails details = new OfferDetails(companyId, item.title(), item.location(),
          item.publicationDate(), item.link(), item.description());
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

  private Map<String, OfferEntity> loadExisting(List<Candidate> candidates)
  {
    if (candidates.isEmpty())
    {
      return Map.of();
    }

    Set<String> keys = new HashSet<>();
    candidates.forEach(candidate -> keys.add(candidate.offer().dedupKey().value()));

    Map<String, OfferEntity> byKey = new HashMap<>();
    offers.findByCreatedByAndContentHashIn(SYSTEM, keys)
      .forEach(entity -> byKey.put(entity.getContentHash(), entity));
    return byKey;
  }

  private static String reason(RuntimeException exception)
  {
    return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
  }

  private record Candidate(ScannedOffer source, Offer offer)
  {
  }
}
