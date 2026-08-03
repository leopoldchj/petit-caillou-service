package com.petitcaillou.domain.user;

public final class HashedPassword
{
  private final String value;

  private HashedPassword(String value)
  {
    this.value = value;
  }

  public static HashedPassword of(String value)
  {
    if (value == null || value.isBlank())
    {
      throw new IllegalArgumentException("Hashed password must not be blank");
    }
    return new HashedPassword(value);
  }

  public String value()
  {
    return value;
  }
}
