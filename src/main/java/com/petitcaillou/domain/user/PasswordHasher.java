package com.petitcaillou.domain.user;

public interface PasswordHasher
{
  HashedPassword hash(RawPassword rawPassword);

  boolean matches(RawPassword rawPassword, HashedPassword hashedPassword);
}
