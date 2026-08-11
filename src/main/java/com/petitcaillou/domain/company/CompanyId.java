package com.petitcaillou.domain.company;

import java.util.UUID;

public final class CompanyId
{
  private final UUID value;

  private CompanyId(UUID value)
  {
    this.value = value;
  }

  public static CompanyId of(UUID value)
  {
    if (value == null)
    {
      throw new IllegalArgumentException("Company id must not be null");
    }
    return new CompanyId(value);
  }

  public static CompanyId newId()
  {
    return new CompanyId(UUID.randomUUID());
  }

  public UUID value()
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
    if (!(other instanceof CompanyId id))
    {
      return false;
    }
    return value.equals(id.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
