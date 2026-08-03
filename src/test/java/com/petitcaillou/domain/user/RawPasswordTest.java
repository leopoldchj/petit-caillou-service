package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawPasswordTest
{
  @Test
  void given_blankValue_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> RawPassword.of("   "))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_value_when_creating_then_keepsIt()
  {
    RawPassword password = RawPassword.of("Secret1");

    assertThat(password.value()).isEqualTo("Secret1");
  }
}
