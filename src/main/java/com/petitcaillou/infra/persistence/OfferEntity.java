package com.petitcaillou.infra.persistence;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "offers")
public class OfferEntity
{
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  @Column(name = "company_id", nullable = false, length = 36)
  private String companyId;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "location", length = 255)
  private String location;

  @Column(name = "publication_date", nullable = false)
  private LocalDate publicationDate;

  @Column(name = "link", length = 2048)
  private String link;

  @Column(name = "description", length = 4000)
  private String description;

  @Column(name = "created_by", nullable = false, length = 30)
  private String createdBy;

  @Column(name = "verified", nullable = false)
  private boolean verified;

  @Column(name = "content_hash", nullable = false, length = 64)
  private String contentHash;

  protected OfferEntity()
  {
  }

  public OfferEntity(String id, String companyId, String title, String location, LocalDate publicationDate,
    String link, String description, String createdBy, boolean verified, String contentHash)
  {
    this.id = id;
    this.companyId = companyId;
    this.title = title;
    this.location = location;
    this.publicationDate = publicationDate;
    this.link = link;
    this.description = description;
    this.createdBy = createdBy;
    this.verified = verified;
    this.contentHash = contentHash;
  }

  public String getId()
  {
    return id;
  }

  public String getCompanyId()
  {
    return companyId;
  }

  public String getTitle()
  {
    return title;
  }

  public String getLocation()
  {
    return location;
  }

  public LocalDate getPublicationDate()
  {
    return publicationDate;
  }

  public String getLink()
  {
    return link;
  }

  public String getDescription()
  {
    return description;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public boolean isVerified()
  {
    return verified;
  }

  public String getContentHash()
  {
    return contentHash;
  }
}
