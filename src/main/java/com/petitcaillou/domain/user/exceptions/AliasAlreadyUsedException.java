package com.petitcaillou.domain.user.exceptions;

public class AliasAlreadyUsedException extends RuntimeException
{
  public AliasAlreadyUsedException()
  {
    super("Alias is already taken");
  }
}
