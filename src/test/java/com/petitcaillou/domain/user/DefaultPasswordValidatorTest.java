package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.user.exceptions.WeakPasswordException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultPasswordValidatorTest
{
  private final DefaultPasswordValidator validator = new DefaultPasswordValidator();

  @Test
  void given_passwordShorterThanSix_when_validating_then_throwsWeakPassword()
  {
    assertThatThrownBy(() -> validator.validate(RawPassword.of("Ab1")))
      .isInstanceOf(WeakPasswordException.class);
  }

  @Test
  void given_passwordWithoutDigit_when_validating_then_throwsWeakPassword()
  {
    assertThatThrownBy(() -> validator.validate(RawPassword.of("Abcdef")))
      .isInstanceOf(WeakPasswordException.class);
  }

  @Test
  void given_passwordWithoutUppercase_when_validating_then_throwsWeakPassword()
  {
    assertThatThrownBy(() -> validator.validate(RawPassword.of("abcde1")))
      .isInstanceOf(WeakPasswordException.class);
  }

  @Test
  void given_passwordMeetingAllRules_when_validating_then_doesNotThrow()
  {
    assertThatCode(() -> validator.validate(RawPassword.of("Abcde1")))
      .doesNotThrowAnyException();
  }
}
