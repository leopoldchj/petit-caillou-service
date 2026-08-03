package com.petitcaillou.domain.user;

public final class RawPassword
{
  private final String value;

  private RawPassword(String value)
  {
    this.value = value;
  }

  public static RawPassword of(String raw)
  {
    if (raw == null || raw.isBlank())
    {
      throw new IllegalArgumentException("Password must not be blank");
    }
    return new RawPassword(raw);
  }

  public String value()
  {
    return value;
  }
}
