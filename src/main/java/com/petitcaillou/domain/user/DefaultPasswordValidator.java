package com.petitcaillou.domain.user;

import com.petitcaillou.domain.user.exceptions.WeakPasswordException;

public class DefaultPasswordValidator implements PasswordValidator
{
  private static final int MIN_LENGTH = 6;

  @Override
  public void validate(RawPassword password)
  {
    String value = password.value();

    if (value.length() < MIN_LENGTH || !hasDigit(value) || !hasUppercase(value))
    {
      throw new WeakPasswordException();
    }
  }

  private boolean hasDigit(String value)
  {
    return value.chars().anyMatch(Character::isDigit);
  }

  private boolean hasUppercase(String value)
  {
    return value.chars().anyMatch(Character::isUpperCase);
  }
}
