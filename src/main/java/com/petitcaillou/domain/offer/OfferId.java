package com.petitcaillou.domain.offer;

import java.util.UUID;

public final class OfferId
{
  private final UUID value;

  private OfferId(UUID value)
  {
    this.value = value;
  }

  public static OfferId of(UUID value)
  {
    if (value == null)
    {
      throw new IllegalArgumentException("Offer id must not be null");
    }
    return new OfferId(value);
  }

  public static OfferId newId()
  {
    return new OfferId(UUID.randomUUID());
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
    if (!(other instanceof OfferId id))
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
