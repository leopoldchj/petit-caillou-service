package com.petitcaillou.domain.authentication.exceptions;

public class InvalidCredentialsException extends RuntimeException
{
  public InvalidCredentialsException()
  {
    super("Username or password is incorrect");
  }
}
