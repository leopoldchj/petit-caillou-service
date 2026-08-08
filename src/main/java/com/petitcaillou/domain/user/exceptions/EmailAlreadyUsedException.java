package com.petitcaillou.domain.user.exceptions;

public class EmailAlreadyUsedException extends RuntimeException
{
  public EmailAlreadyUsedException()
  {
    super("Email is already registered");
  }
}
