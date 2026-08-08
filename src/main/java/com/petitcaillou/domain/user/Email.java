package com.petitcaillou.domain.user;

import java.util.regex.Pattern;

public final class Email
{
  private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final String value;

  private Email(String value)
  {
    this.value = value;
  }

  public static Email of(String raw)
  {
    if (raw == null)
    {
      throw new IllegalArgumentException("Email must not be null");
    }

    String normalized = raw.trim().toLowerCase();

    if (!FORMAT.matcher(normalized).matches())
    {
      throw new IllegalArgumentException("Email format is invalid");
    }

    return new Email(normalized);
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
    if (!(other instanceof Email email))
    {
      return false;
    }
    return value.equals(email.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
