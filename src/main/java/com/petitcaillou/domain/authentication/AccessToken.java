package com.petitcaillou.domain.authentication;

public final class AccessToken
{
  private final String value;

  private AccessToken(String value)
  {
    this.value = value;
  }

  public static AccessToken of(String value)
  {
    if (value == null || value.isBlank())
    {
      throw new IllegalArgumentException("Access token must not be blank");
    }
    return new AccessToken(value);
  }

  public String value()
  {
    return value;
  }
}
