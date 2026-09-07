package com.petitcaillou.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class CompanyEntity
{
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "normalized_name", nullable = false, unique = true, length = 255)
  private String normalizedName;

  @Column(name = "website", length = 2048)
  private String website;

  protected CompanyEntity()
  {
  }

  public CompanyEntity(String id, String name, String normalizedName, String website)
  {
    this.id = id;
    this.name = name;
    this.normalizedName = normalizedName;
    this.website = website;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getNormalizedName()
  {
    return normalizedName;
  }

  public String getWebsite()
  {
    return website;
  }
}
