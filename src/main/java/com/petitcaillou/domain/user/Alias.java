package com.petitcaillou.domain.user;

import java.util.regex.Pattern;

public final class Alias
{
  private static final Pattern FORMAT = Pattern.compile("^[a-zA-Z0-9_.-]{3,30}$");

  private final String value;

  private Alias(String value)
  {
    this.value = value;
  }

  public static Alias of(String raw)
  {
    if (raw == null)
    {
      throw new IllegalArgumentException("Alias must not be null");
    }

    String trimmed = raw.trim();

    if (!FORMAT.matcher(trimmed).matches())
    {
      throw new IllegalArgumentException("Alias must be 3 to 30 characters of letters, digits, dot, dash or underscore");
    }

    return new Alias(trimmed);
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
    if (!(other instanceof Alias alias))
    {
      return false;
    }
    return value.equals(alias.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
