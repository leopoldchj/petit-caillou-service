package com.petitcaillou.infra.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.petitcaillou.domain.offer.CatalogWriter;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;

@Component
public class CatalogWriterAdapter implements CatalogWriter
{
  static final int CHUNK_SIZE = 50;

  private final OfferBatchWriter batch;
  private final TransactionTemplate transactions;

  public CatalogWriterAdapter(OfferBatchWriter batch, TransactionTemplate transactions)
  {
    this.batch = batch;
    this.transactions = transactions;
  }

  @Override
  public List<OfferWriteResult> ingestAll(List<ScannedOffer> scanned)
  {
    List<OfferWriteResult> results = new ArrayList<>();
    for (int start = 0; start < scanned.size(); start += CHUNK_SIZE)
    {
      results.addAll(writeOrIsolate(scanned.subList(start, Math.min(start + CHUNK_SIZE, scanned.size()))));
    }
    return results;
  }

  private List<OfferWriteResult> writeOrIsolate(List<ScannedOffer> chunk)
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
        results.add(OfferWriteResult.failed(item,
          failure.getMessage() == null ? failure.getClass().getSimpleName() : failure.getMessage()));
      }
    }
    return results;
  }

  private List<OfferWriteResult> inTransaction(List<ScannedOffer> chunk)
  {
    List<OfferWriteResult> written = transactions.execute(status -> batch.write(chunk));
    return written == null ? List.of() : written;
  }
}
