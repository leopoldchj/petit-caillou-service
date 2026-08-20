package com.petitcaillou.infra.persistence;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.petitcaillou.domain.jobapplication.ResponseStatus;

@Entity
@Table(name = "job_applications")
public class JobApplicationEntity
{
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  @Column(name = "owner_username", nullable = false, updatable = false, length = 30)
  private String ownerUsername;

  @Column(name = "company_id", nullable = false, length = 36)
  private String companyId;

  @Column(name = "link", length = 2048)
  private String link;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "description", length = 2000)
  private String description;

  @Column(name = "location", length = 255)
  private String location;

  @Column(name = "application_date")
  private LocalDate applicationDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "response_status", nullable = false, length = 20)
  private ResponseStatus responseStatus;

  protected JobApplicationEntity()
  {
  }

  public JobApplicationEntity(String id, String ownerUsername, String companyId, String link, String title,
    String description, String location, LocalDate applicationDate, ResponseStatus responseStatus)
  {
    this.id = id;
    this.ownerUsername = ownerUsername;
    this.companyId = companyId;
    this.link = link;
    this.title = title;
    this.description = description;
    this.location = location;
    this.applicationDate = applicationDate;
    this.responseStatus = responseStatus;
  }

  public String getId()
  {
    return id;
  }

  public String getOwnerUsername()
  {
    return ownerUsername;
  }

  public String getCompanyId()
  {
    return companyId;
  }

  public String getLink()
  {
    return link;
  }

  public String getTitle()
  {
    return title;
  }

  public String getDescription()
  {
    return description;
  }

  public String getLocation()
  {
    return location;
  }

  public LocalDate getApplicationDate()
  {
    return applicationDate;
  }

  public ResponseStatus getResponseStatus()
  {
    return responseStatus;
  }
}
