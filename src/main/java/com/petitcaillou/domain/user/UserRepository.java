package com.petitcaillou.domain.user;

import java.util.Optional;

public interface UserRepository
{
  boolean existsByUsername(Username username);

  boolean existsByEmail(Email email);

  Optional<User> findByUsername(Username username);

  User save(User user);
}
