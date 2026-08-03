package com.petitcaillou.domain.user;

import java.util.Optional;

public interface UserRepository
{
  boolean existsByAlias(Alias alias);

  boolean existsByEmail(Email email);

  Optional<User> findByAlias(Alias alias);

  User save(User user);
}
