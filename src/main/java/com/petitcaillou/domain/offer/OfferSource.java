package com.petitcaillou.domain.offer;

import java.time.LocalDate;

public interface OfferSource
{
  SourcePage fetch(LocalDate since, LocalDate until, String cursor);
}
