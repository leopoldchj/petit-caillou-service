package com.petitcaillou.domain.authentication.exceptions;

public class InvalidCredentialsException extends RuntimeException
{
  public InvalidCredentialsException()
  {
    super("Alias or password is incorrect");
  }
}
