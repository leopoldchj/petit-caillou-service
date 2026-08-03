package com.petitcaillou.infra.persistence;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest
{
  private static final Alias ALIAS = Alias.of("john_doe");

  @Test
  void given_domainUser_when_mappingToEntity_then_copiesAlias()
  {
    User user = User.reconstitute(ALIAS, Email.of("a@b.com"), HashedPassword.of("h"), Role.USER);

    UserEntity entity = UserMapper.toEntity(user);

    assertThat(entity.getAlias()).isEqualTo("john_doe");
  }

  @Test
  void given_entity_when_mappingToDomain_then_copiesAlias()
  {
    UserEntity entity = new UserEntity("john_doe", "a@b.com", "h", Role.USER);

    User user = UserMapper.toDomain(entity);

    assertThat(user.alias().value()).isEqualTo("john_doe");
  }
}
