package com.petitcaillou.domain.authentication;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticatedUserTest
{
  private static final Username USERNAME = Username.of("john_doe");
  private static final Email EMAIL = Email.of("user@app.com");

  @Test
  void given_nullRole_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> new AuthenticatedUser(USERNAME, EMAIL, null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_validData_when_creating_then_exposesUsername()
  {
    AuthenticatedUser current = new AuthenticatedUser(USERNAME, EMAIL, Role.USER);

    assertThat(current.username()).isEqualTo(USERNAME);
  }
}
