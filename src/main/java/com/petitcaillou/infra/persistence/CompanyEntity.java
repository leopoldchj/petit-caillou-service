package com.petitcaillou.infra.persistence;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "companies")
public class CompanyEntity implements Persistable<String>
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

  @Transient
  private boolean isNew;

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

  CompanyEntity asNew()
  {
    this.isNew = true;
    return this;
  }

  @Override
  public String getId()
  {
    return id;
  }

  @Override
  public boolean isNew()
  {
    return isNew;
  }

  @PostLoad
  @PostPersist
  void markPersisted()
  {
    this.isNew = false;
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
