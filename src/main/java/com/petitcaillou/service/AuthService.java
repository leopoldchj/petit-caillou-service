package com.petitcaillou.service;

import com.petitcaillou.domain.authentication.AccessToken;
import com.petitcaillou.domain.authentication.TokenIssuer;
import com.petitcaillou.domain.authentication.exceptions.InvalidCredentialsException;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.PasswordHasher;
import com.petitcaillou.domain.user.PasswordValidator;
import com.petitcaillou.domain.user.RawPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.domain.user.UserRepository;
import com.petitcaillou.domain.user.exceptions.UsernameAlreadyUsedException;
import com.petitcaillou.domain.user.exceptions.EmailAlreadyUsedException;

public class AuthService
{
  private final UserRepository users;
  private final PasswordValidator passwordValidator;
  private final PasswordHasher passwordHasher;
  private final TokenIssuer tokenIssuer;

  public AuthService(UserRepository users, PasswordValidator passwordValidator, PasswordHasher passwordHasher,
    TokenIssuer tokenIssuer)
  {
    this.users = users;
    this.passwordValidator = passwordValidator;
    this.passwordHasher = passwordHasher;
    this.tokenIssuer = tokenIssuer;
  }

  public AccessToken register(Username username, Email email, RawPassword rawPassword)
  {
    passwordValidator.validate(rawPassword);

    if (users.existsByUsername(username))
    {
      throw new UsernameAlreadyUsedException();
    }

    if (users.existsByEmail(email))
    {
      throw new EmailAlreadyUsedException();
    }

    User user = users.save(User.register(username, email, rawPassword, Role.USER, passwordHasher));
    return tokenIssuer.issueFor(user);
  }

  public AccessToken login(Username username, RawPassword rawPassword)
  {
    User user = users.findByUsername(username)
      .orElseThrow(InvalidCredentialsException::new);

    if (!user.hasPassword(rawPassword, passwordHasher))
    {
      throw new InvalidCredentialsException();
    }

    return tokenIssuer.issueFor(user);
  }
}
