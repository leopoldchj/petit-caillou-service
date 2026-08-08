package com.petitcaillou.infra.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.PasswordHasher;
import com.petitcaillou.domain.user.RawPassword;

@Component
public class BCryptPasswordHasher implements PasswordHasher
{
  private final PasswordEncoder encoder;

  public BCryptPasswordHasher(PasswordEncoder encoder)
  {
    this.encoder = encoder;
  }

  @Override
  public HashedPassword hash(RawPassword rawPassword)
  {
    return HashedPassword.of(encoder.encode(rawPassword.value()));
  }

  @Override
  public boolean matches(RawPassword rawPassword, HashedPassword hashedPassword)
  {
    return encoder.matches(rawPassword.value(), hashedPassword.value());
  }
}
