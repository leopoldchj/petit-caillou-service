package com.petitcaillou.infra.persistence;

import com.petitcaillou.domain.user.Username;
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
      user.username().value(),
      user.email().value(),
      user.password().value(),
      user.role());
  }

  static User toDomain(UserEntity entity)
  {
    return User.reconstitute(
      Username.of(entity.getUsername()),
      Email.of(entity.getEmail()),
      HashedPassword.of(entity.getPasswordHash()),
      entity.getRole());
  }
}
