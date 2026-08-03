package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest
{
  @Test
  void given_addressWithUppercaseAndSpaces_when_creatingEmail_then_normalizesToTrimmedLowercase()
  {
    Email email = Email.of("  User@Example.COM ");

    assertThat(email.value()).isEqualTo("user@example.com");
  }

  @Test
  void given_stringWithoutAtSign_when_creatingEmail_then_rejectsIt()
  {
    assertThatThrownBy(() -> Email.of("not-an-email"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_nullValue_when_creatingEmail_then_rejectsIt()
  {
    assertThatThrownBy(() -> Email.of(null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_sameNormalizedAddress_when_comparingTwoEmails_then_theyAreEqual()
  {
    assertThat(Email.of("User@Example.com")).isEqualTo(Email.of("user@example.com"));
  }

  @Test
  void given_differentAddress_when_comparingTwoEmails_then_theyAreNotEqual()
  {
    assertThat(Email.of("a@b.com")).isNotEqualTo(Email.of("c@d.com"));
  }
}
