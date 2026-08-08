package com.petitcaillou.domain.user.exceptions;

public class WeakPasswordException extends RuntimeException
{
  public WeakPasswordException()
  {
    super("Password must be at least 6 characters and include a digit and an uppercase letter");
  }
}
