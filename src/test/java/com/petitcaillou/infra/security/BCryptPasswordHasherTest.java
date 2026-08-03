package com.petitcaillou.infra.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.RawPassword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BCryptPasswordHasherTest
{
  private final PasswordEncoder encoder = mock(PasswordEncoder.class);
  private final BCryptPasswordHasher hasher = new BCryptPasswordHasher(encoder);

  @Test
  void given_rawPassword_when_hashing_then_wrapsEncoderOutput()
  {
    when(encoder.encode("password1")).thenReturn("bcrypt-hash");

    HashedPassword hashed = hasher.hash(RawPassword.of("password1"));

    assertThat(hashed.value()).isEqualTo("bcrypt-hash");
  }

  @Test
  void given_matchingRawPassword_when_checkingMatches_then_returnsTrue()
  {
    when(encoder.matches("password1", "bcrypt-hash")).thenReturn(true);

    boolean result = hasher.matches(RawPassword.of("password1"), HashedPassword.of("bcrypt-hash"));

    assertThat(result).isTrue();
  }
}
