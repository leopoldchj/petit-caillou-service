package com.petitcaillou.infra.offer;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.offer.ScannedOffer;

import static org.assertj.core.api.Assertions.assertThat;

class FakeOfferSourceTest
{
  private final FakeOfferSource source = new FakeOfferSource();

  @Test
  void given_window_when_fetching_then_returnsAnOfferWithinIt()
  {
    LocalDate since = LocalDate.of(2026, 1, 1);

    ScannedOffer offer = source.fetch(since, LocalDate.of(2026, 1, 31), null).items().getFirst();

    assertThat(offer.publicationDate()).isEqualTo(since);
  }
}
