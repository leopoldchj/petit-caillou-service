package com.petitcaillou.domain.offer;

import java.util.List;

public interface CatalogWriter
{
  List<OfferWriteResult> ingestAll(List<ScannedOffer> scanned);
}
