package com.petitcaillou.domain.user.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException
{
  public UsernameAlreadyUsedException()
  {
    super("Username is already taken");
  }
}
