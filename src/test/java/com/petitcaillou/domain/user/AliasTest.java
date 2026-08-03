package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AliasTest
{
  @Test
  void given_stringWithForbiddenCharacters_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> Alias.of("bad alias!"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_tooShortString_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> Alias.of("ab"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_validString_when_creating_then_keepsIt()
  {
    Alias alias = Alias.of("john_doe");

    assertThat(alias.value()).isEqualTo("john_doe");
  }

  @Test
  void given_sameValue_when_comparingTwoAliases_then_theyAreEqual()
  {
    assertThat(Alias.of("john_doe")).isEqualTo(Alias.of("john_doe"));
  }
}
