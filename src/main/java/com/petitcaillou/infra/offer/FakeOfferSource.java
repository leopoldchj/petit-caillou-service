package com.petitcaillou.infra.offer;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SourcePage;

@Component
public class FakeOfferSource implements OfferSource
{
  @Override
  public SourcePage fetch(LocalDate since, LocalDate until, String cursor)
  {
    LocalDate publicationDate = since != null ? since : LocalDate.now();
    ScannedOffer offer = new ScannedOffer(
      "Acme",
      "https://acme.example",
      "Backend Engineer",
      "Paris, France",
      publicationDate,
      "https://boards.example/acme/backend-engineer",
      "Example offer produced by the fake source.",
      "fake",
      "backend-engineer");
    return new SourcePage(List.of(offer), null);
  }
}
