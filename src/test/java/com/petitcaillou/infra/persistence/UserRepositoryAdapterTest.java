package com.petitcaillou.infra.persistence;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRepositoryAdapterTest
{
  private static final Alias ALIAS = Alias.of("john_doe");

  private final SpringDataUserRepository jpa = mock(SpringDataUserRepository.class);
  private final UserRepositoryAdapter adapter = new UserRepositoryAdapter(jpa);

  @Test
  void given_existingAlias_when_checkingExistence_then_delegatesToJpa()
  {
    when(jpa.existsByAlias("john_doe")).thenReturn(true);

    boolean result = adapter.existsByAlias(ALIAS);

    assertThat(result).isTrue();
  }

  @Test
  void given_existingEmail_when_checkingExistence_then_delegatesToJpa()
  {
    when(jpa.existsByEmail("a@b.com")).thenReturn(true);

    boolean result = adapter.existsByEmail(Email.of("a@b.com"));

    assertThat(result).isTrue();
  }

  @Test
  void given_storedEntity_when_findingByAlias_then_returnsMappedUser()
  {
    UserEntity entity = new UserEntity("john_doe", "a@b.com", "h", Role.USER);
    when(jpa.findByAlias("john_doe")).thenReturn(Optional.of(entity));

    Optional<User> found = adapter.findByAlias(ALIAS);

    assertThat(found.orElseThrow().alias().value()).isEqualTo("john_doe");
  }

  @Test
  void given_missingAlias_when_findingByAlias_then_returnsEmpty()
  {
    when(jpa.findByAlias("john_doe")).thenReturn(Optional.empty());

    Optional<User> found = adapter.findByAlias(ALIAS);

    assertThat(found).isEmpty();
  }

  @Test
  void given_user_when_saving_then_returnsMappedSavedUser()
  {
    User user = User.reconstitute(ALIAS, Email.of("a@b.com"), HashedPassword.of("h"), Role.USER);
    when(jpa.save(any(UserEntity.class))).thenReturn(new UserEntity("john_doe", "a@b.com", "h", Role.USER));

    User saved = adapter.save(user);

    assertThat(saved.alias().value()).isEqualTo("john_doe");
  }
}
