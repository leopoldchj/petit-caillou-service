package com.petitcaillou.domain.company;

import com.petitcaillou.domain.web.Url;

public final class Company
{
  private final CompanyId id;
  private final String name;
  private final NormalizedName normalizedName;
  private final Url website;

  private Company(CompanyId id, String name, String website)
  {
    this.id = id;
    this.name = requireName(name);
    this.normalizedName = NormalizedName.of(this.name);
    this.website = Url.ofNullable(website);
  }

  public static Company create(String name, String website)
  {
    return new Company(CompanyId.newId(), name, website);
  }

  public static Company reconstitute(CompanyId id, String name, String website)
  {
    return new Company(id, name, website);
  }

  private static String requireName(String name)
  {
    if (name == null || name.isBlank())
    {
      throw new IllegalArgumentException("Company name must not be blank");
    }
    return name.trim();
  }

  public CompanyId id()
  {
    return id;
  }

  public String name()
  {
    return name;
  }

  public NormalizedName normalizedName()
  {
    return normalizedName;
  }

  public Url website()
  {
    return website;
  }
}
