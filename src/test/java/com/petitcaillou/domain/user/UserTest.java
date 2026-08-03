package com.petitcaillou.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserTest
{
  private static final Alias ALIAS = Alias.of("john_doe");
  private static final Email EMAIL = Email.of("a@b.com");
  private static final RawPassword RAW_PASSWORD = RawPassword.of("password1");

  private final PasswordHasher hasher = mock(PasswordHasher.class);

  @Test
  void given_rawPassword_when_registering_then_storesTheHashedPassword()
  {
    when(hasher.hash(RAW_PASSWORD)).thenReturn(HashedPassword.of("hashed-value"));

    User user = User.register(ALIAS, EMAIL, RAW_PASSWORD, Role.USER, hasher);

    assertThat(user.password().value()).isEqualTo("hashed-value");
  }

  @Test
  void given_matchingPassword_when_checkingHasPassword_then_returnsTrue()
  {
    HashedPassword stored = HashedPassword.of("stored-hash");
    User user = User.reconstitute(ALIAS, EMAIL, stored, Role.USER);
    when(hasher.matches(RAW_PASSWORD, stored)).thenReturn(true);

    boolean result = user.hasPassword(RAW_PASSWORD, hasher);

    assertThat(result).isTrue();
  }
}
