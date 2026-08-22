package com.petitcaillou.domain.company;

public final class Company
{
  private final CompanyId id;
  private final String name;
  private final String website;

  private Company(CompanyId id, String name, String website)
  {
    this.id = id;
    this.name = name;
    this.website = website;
  }

  public static Company create(String name, String website)
  {
    return new Company(CompanyId.newId(), requireName(name), website);
  }

  public static Company reconstitute(CompanyId id, String name, String website)
  {
    return new Company(id, requireName(name), website);
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

  public String website()
  {
    return website;
  }
}
