package com.petitcaillou.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.user.Alias;
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
  private static final Alias ALIAS = Alias.of("john_doe");

  private final UserRepository users = mock(UserRepository.class);
  private final UserService userService = new UserService(users);

  @Test
  void given_existingAlias_when_gettingByAlias_then_returnsTheUser()
  {
    User stored = User.reconstitute(ALIAS, Email.of("user@app.com"), HashedPassword.of("h"), Role.USER);
    when(users.findByAlias(ALIAS)).thenReturn(Optional.of(stored));

    User found = userService.byAlias(ALIAS);

    assertThat(found).isEqualTo(stored);
  }

  @Test
  void given_missingAlias_when_gettingByAlias_then_throwsUserNotFound()
  {
    when(users.findByAlias(ALIAS)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.byAlias(ALIAS))
      .isInstanceOf(UserNotFoundException.class);
  }
}
