package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashedPasswordTest
{
  @Test
  void given_blankValue_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> HashedPassword.of("  "))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_value_when_creating_then_keepsIt()
  {
    HashedPassword hashed = HashedPassword.of("hash");

    assertThat(hashed.value()).isEqualTo("hash");
  }
}
