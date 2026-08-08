package com.petitcaillou.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.domain.user.UserRepository;
import com.petitcaillou.domain.user.exceptions.UserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest
{
  private static final Username USERNAME = Username.of("john_doe");

  private final UserRepository users = mock(UserRepository.class);
  private final UserService userService = new UserService(users);

  @Test
  void given_existingUsername_when_gettingByUsername_then_returnsTheUser()
  {
    User stored = User.reconstitute(USERNAME, Email.of("user@app.com"), HashedPassword.of("h"), Role.USER);
    when(users.findByUsername(USERNAME)).thenReturn(Optional.of(stored));

    User found = userService.byUsername(USERNAME);

    assertThat(found).isEqualTo(stored);
  }

  @Test
  void given_missingUsername_when_gettingByUsername_then_throwsUserNotFound()
  {
    when(users.findByUsername(USERNAME)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.byUsername(USERNAME))
      .isInstanceOf(UserNotFoundException.class);
  }
}
