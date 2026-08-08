package com.petitcaillou.application;

import org.junit.jupiter.api.Test;

import com.petitcaillou.application.dto.CurrentUserView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountResourceTest
{
  private static final Username USERNAME = Username.of("john_doe");
  private static final Email EMAIL = Email.of("me@app.com");

  private final UserService userService = mock(UserService.class);
  private final AccountResource resource = new AccountResource(userService);

  @Test
  void given_authenticatedUser_when_gettingMe_then_returnsUsernameFromService()
  {
    User stored = User.reconstitute(USERNAME, EMAIL, HashedPassword.of("h"), Role.USER);
    when(userService.byUsername(USERNAME)).thenReturn(stored);

    CurrentUserView view = resource.me(new AuthenticatedUser(USERNAME, EMAIL, Role.USER));

    assertThat(view.username()).isEqualTo("john_doe");
  }
}
