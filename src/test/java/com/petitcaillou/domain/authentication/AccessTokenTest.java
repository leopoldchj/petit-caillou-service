package com.petitcaillou.domain.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessTokenTest
{
  @Test
  void given_blankValue_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> AccessToken.of("  "))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_value_when_creating_then_keepsIt()
  {
    AccessToken token = AccessToken.of("abc");

    assertThat(token.value()).isEqualTo("abc");
  }
}
