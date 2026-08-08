package com.petitcaillou.domain.user;

import java.util.regex.Pattern;

public final class Username
{
  private static final Pattern FORMAT = Pattern.compile("^[a-zA-Z0-9_.-]{3,30}$");

  private final String value;

  private Username(String value)
  {
    this.value = value;
  }

  public static Username of(String raw)
  {
    if (raw == null)
    {
      throw new IllegalArgumentException("Username must not be null");
    }

    String trimmed = raw.trim();

    if (!FORMAT.matcher(trimmed).matches())
    {
      throw new IllegalArgumentException("Username must be 3 to 30 characters of letters, digits, dot, dash or underscore");
    }

    return new Username(trimmed);
  }

  public String value()
  {
    return value;
  }

  @Override
  public boolean equals(Object other)
  {
    if (this == other)
    {
      return true;
    }
    if (!(other instanceof Username username))
    {
      return false;
    }
    return value.equals(username.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
