package com.petitcaillou.infra.persistence;

import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.User;

final class UserMapper
{
  private UserMapper()
  {
  }

  static UserEntity toEntity(User user)
  {
    return new UserEntity(
      user.alias().value(),
      user.email().value(),
      user.password().value(),
      user.role());
  }

  static User toDomain(UserEntity entity)
  {
    return User.reconstitute(
      Alias.of(entity.getAlias()),
      Email.of(entity.getEmail()),
      HashedPassword.of(entity.getPasswordHash()),
      entity.getRole());
  }
}
