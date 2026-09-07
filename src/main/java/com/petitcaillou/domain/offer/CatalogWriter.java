package com.petitcaillou.domain.offer;

public interface CatalogWriter
{
  OfferWriteResult ingest(ScannedOffer scanned);
}
