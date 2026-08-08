package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsernameTest
{
  @Test
  void given_stringWithForbiddenCharacters_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> Username.of("bad username!"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_tooShortString_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> Username.of("ab"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_validString_when_creating_then_keepsIt()
  {
    Username username = Username.of("john_doe");

    assertThat(username.value()).isEqualTo("john_doe");
  }

  @Test
  void given_sameValue_when_comparingTwoUsernamees_then_theyAreEqual()
  {
    assertThat(Username.of("john_doe")).isEqualTo(Username.of("john_doe"));
  }
}
