package com.petitcaillou.domain.offer;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferIdTest
{
  @Test
  void given_uuid_when_creating_then_keepsTheValue()
  {
    UUID value = UUID.randomUUID();

    assertThat(OfferId.of(value).value()).isEqualTo(value);
  }

  @Test
  void given_null_when_creating_then_throws()
  {
    assertThatThrownBy(() -> OfferId.of(null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_sameValue_when_comparing_then_areEqual()
  {
    UUID value = UUID.randomUUID();

    assertThat(OfferId.of(value)).isEqualTo(OfferId.of(value));
  }
}
