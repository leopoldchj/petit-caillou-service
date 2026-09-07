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

  @Column(name = "offer_id", nullable = false, updatable = false, length = 36)
  private String offerId;

  @Column(name = "application_date")
  private LocalDate applicationDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "response_status", nullable = false, length = 20)
  private ResponseStatus responseStatus;

  @Column(name = "notes", length = 2000)
  private String notes;

  protected JobApplicationEntity()
  {
  }

  public JobApplicationEntity(String id, String ownerUsername, String offerId, LocalDate applicationDate,
    ResponseStatus responseStatus, String notes)
  {
    this.id = id;
    this.ownerUsername = ownerUsername;
    this.offerId = offerId;
    this.applicationDate = applicationDate;
    this.responseStatus = responseStatus;
    this.notes = notes;
  }

  public String getId()
  {
    return id;
  }

  public String getOwnerUsername()
  {
    return ownerUsername;
  }

  public String getOfferId()
  {
    return offerId;
  }

  public LocalDate getApplicationDate()
  {
    return applicationDate;
  }

  public ResponseStatus getResponseStatus()
  {
    return responseStatus;
  }

  public String getNotes()
  {
    return notes;
  }
}
