package com.petitcaillou.domain.jobapplication;

import java.util.UUID;

public final class JobApplicationId
{
  private final UUID value;

  private JobApplicationId(UUID value)
  {
    this.value = value;
  }

  public static JobApplicationId of(UUID value)
  {
    if (value == null)
    {
      throw new IllegalArgumentException("Job application id must not be null");
    }
    return new JobApplicationId(value);
  }

  public static JobApplicationId newId()
  {
    return new JobApplicationId(UUID.randomUUID());
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
    if (!(other instanceof JobApplicationId id))
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
