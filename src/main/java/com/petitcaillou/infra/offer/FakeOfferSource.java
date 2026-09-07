package com.petitcaillou.infra.offer;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.ScannedOffer;

@Component
public class FakeOfferSource implements OfferSource
{
  @Override
  public List<ScannedOffer> fetch(LocalDate since, LocalDate until)
  {
    LocalDate publicationDate = since != null ? since : LocalDate.now();
    return List.of(new ScannedOffer(
      "Acme",
      "https://acme.example",
      "Backend Engineer",
      "Paris, France",
      publicationDate,
      "https://boards.example/acme/backend-engineer",
      "Example offer produced by the fake source."));
  }
}
