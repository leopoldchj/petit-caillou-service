package com.petitcaillou.domain.user.exceptions;

public class UserNotFoundException extends RuntimeException
{
  public UserNotFoundException()
  {
    super("User does not exist");
  }
}
