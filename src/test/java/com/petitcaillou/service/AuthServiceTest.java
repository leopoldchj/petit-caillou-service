package com.petitcaillou.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.authentication.AccessToken;
import com.petitcaillou.domain.authentication.TokenIssuer;
import com.petitcaillou.domain.authentication.exceptions.InvalidCredentialsException;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.PasswordHasher;
import com.petitcaillou.domain.user.PasswordValidator;
import com.petitcaillou.domain.user.RawPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.domain.user.UserRepository;
import com.petitcaillou.domain.user.exceptions.UsernameAlreadyUsedException;
import com.petitcaillou.domain.user.exceptions.EmailAlreadyUsedException;
import com.petitcaillou.domain.user.exceptions.WeakPasswordException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest
{
  private static final Username FRESH_USERNAME = Username.of("newbie");
  private static final Username TAKEN_USERNAME = Username.of("taken");
  private static final Username KNOWN_USERNAME = Username.of("known");
  private static final Email EMAIL = Email.of("user@app.com");
  private static final RawPassword RAW_PASSWORD = RawPassword.of("Password1");
  private static final HashedPassword STORED_PASSWORD = HashedPassword.of("hash");
  private static final AccessToken TOKEN = AccessToken.of("signed-token");
  private static final User KNOWN_USER = User.reconstitute(KNOWN_USERNAME, EMAIL, STORED_PASSWORD, Role.USER);

  private final UserRepository users = mock(UserRepository.class);
  private final PasswordValidator passwordValidator = mock(PasswordValidator.class);
  private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
  private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
  private final AuthService authService = new AuthService(users, passwordValidator, passwordHasher, tokenIssuer);

  @Test
  void given_freshUsernameAndEmail_when_registering_then_returnsIssuedToken()
  {
    when(users.existsByUsername(FRESH_USERNAME)).thenReturn(false);
    when(users.existsByEmail(EMAIL)).thenReturn(false);
    when(passwordHasher.hash(any())).thenReturn(STORED_PASSWORD);
    when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenIssuer.issueFor(any())).thenReturn(TOKEN);

    AccessToken token = authService.register(FRESH_USERNAME, EMAIL, RAW_PASSWORD);

    assertThat(token).isEqualTo(TOKEN);
  }

  @Test
  void given_freshUsernameAndEmail_when_registering_then_savesTheNewUser()
  {
    when(users.existsByUsername(FRESH_USERNAME)).thenReturn(false);
    when(users.existsByEmail(EMAIL)).thenReturn(false);
    when(passwordHasher.hash(any())).thenReturn(STORED_PASSWORD);
    when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenIssuer.issueFor(any())).thenReturn(TOKEN);

    authService.register(FRESH_USERNAME, EMAIL, RAW_PASSWORD);

    verify(users).save(any(User.class));
  }

  @Test
  void given_weakPassword_when_registering_then_throwsWeakPassword()
  {
    doThrow(new WeakPasswordException()).when(passwordValidator).validate(any());

    assertThatThrownBy(() -> authService.register(FRESH_USERNAME, EMAIL, RAW_PASSWORD))
      .isInstanceOf(WeakPasswordException.class);
  }

  @Test
  void given_weakPassword_when_registering_then_neverSaves()
  {
    doThrow(new WeakPasswordException()).when(passwordValidator).validate(any());
    catchThrowable(() -> authService.register(FRESH_USERNAME, EMAIL, RAW_PASSWORD));

    verify(users, never()).save(any());
  }

  @Test
  void given_usernameAlreadyTaken_when_registering_then_throwsUsernameAlreadyUsed()
  {
    when(users.existsByUsername(TAKEN_USERNAME)).thenReturn(true);

    assertThatThrownBy(() -> authService.register(TAKEN_USERNAME, EMAIL, RAW_PASSWORD))
      .isInstanceOf(UsernameAlreadyUsedException.class);
  }

  @Test
  void given_usernameAlreadyTaken_when_registering_then_neverSaves()
  {
    when(users.existsByUsername(TAKEN_USERNAME)).thenReturn(true);
    catchThrowable(() -> authService.register(TAKEN_USERNAME, EMAIL, RAW_PASSWORD));

    verify(users, never()).save(any());
  }

  @Test
  void given_emailAlreadyRegistered_when_registering_then_throwsEmailAlreadyUsed()
  {
    when(users.existsByUsername(FRESH_USERNAME)).thenReturn(false);
    when(users.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> authService.register(FRESH_USERNAME, EMAIL, RAW_PASSWORD))
      .isInstanceOf(EmailAlreadyUsedException.class);
  }

  @Test
  void given_validCredentials_when_loggingIn_then_returnsIssuedToken()
  {
    when(users.findByUsername(KNOWN_USERNAME)).thenReturn(Optional.of(KNOWN_USER));
    when(passwordHasher.matches(RAW_PASSWORD, STORED_PASSWORD)).thenReturn(true);
    when(tokenIssuer.issueFor(KNOWN_USER)).thenReturn(TOKEN);

    AccessToken token = authService.login(KNOWN_USERNAME, RAW_PASSWORD);

    assertThat(token).isEqualTo(TOKEN);
  }

  @Test
  void given_unknownUsername_when_loggingIn_then_throwsInvalidCredentials()
  {
    when(users.findByUsername(KNOWN_USERNAME)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(KNOWN_USERNAME, RAW_PASSWORD))
      .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void given_wrongPassword_when_loggingIn_then_throwsInvalidCredentials()
  {
    when(users.findByUsername(KNOWN_USERNAME)).thenReturn(Optional.of(KNOWN_USER));
    when(passwordHasher.matches(RAW_PASSWORD, STORED_PASSWORD)).thenReturn(false);

    assertThatThrownBy(() -> authService.login(KNOWN_USERNAME, RAW_PASSWORD))
      .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void given_wrongPassword_when_loggingIn_then_neverIssuesToken()
  {
    when(users.findByUsername(KNOWN_USERNAME)).thenReturn(Optional.of(KNOWN_USER));
    when(passwordHasher.matches(RAW_PASSWORD, STORED_PASSWORD)).thenReturn(false);
    catchThrowable(() -> authService.login(KNOWN_USERNAME, RAW_PASSWORD));

    verify(tokenIssuer, never()).issueFor(any());
  }
}
