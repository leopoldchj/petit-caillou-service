package com.petitcaillou.domain.offer;

import java.time.LocalDate;
import java.util.List;

public interface OfferSource
{
  List<ScannedOffer> fetch(LocalDate since, LocalDate until);
}
