package com.petitcaillou.infra.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.domain.user.UserRepository;

@Component
public class UserRepositoryAdapter implements UserRepository
{
  private final SpringDataUserRepository jpa;

  public UserRepositoryAdapter(SpringDataUserRepository jpa)
  {
    this.jpa = jpa;
  }

  @Override
  public boolean existsByAlias(Alias alias)
  {
    return jpa.existsByAlias(alias.value());
  }

  @Override
  public boolean existsByEmail(Email email)
  {
    return jpa.existsByEmail(email.value());
  }

  @Override
  public Optional<User> findByAlias(Alias alias)
  {
    return jpa.findByAlias(alias.value()).map(UserMapper::toDomain);
  }

  @Override
  public User save(User user)
  {
    UserEntity saved = jpa.save(UserMapper.toEntity(user));
    return UserMapper.toDomain(saved);
  }
}
