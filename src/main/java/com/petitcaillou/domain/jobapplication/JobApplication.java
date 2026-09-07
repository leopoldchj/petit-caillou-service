package com.petitcaillou.domain.jobapplication;

import java.time.LocalDate;

import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.user.Username;

public final class JobApplication
{
  private final JobApplicationId id;
  private final Username owner;
  private final OfferId offerId;
  private final LocalDate applicationDate;
  private final ResponseStatus responseStatus;
  private final String notes;

  private JobApplication(JobApplicationId id, Username owner, JobApplicationDetails details)
  {
    if (details.offerId() == null)
    {
      throw new IllegalArgumentException("Offer is required");
    }

    this.id = id;
    this.owner = owner;
    this.offerId = details.offerId();
    this.applicationDate = details.applicationDate();
    this.responseStatus = details.responseStatus() == null ? ResponseStatus.NO_RESPONSE : details.responseStatus();
    this.notes = details.notes();
  }

  public static JobApplication create(Username owner, JobApplicationDetails details)
  {
    return new JobApplication(JobApplicationId.newId(), owner, details);
  }

  public static JobApplication reconstitute(JobApplicationId id, Username owner, JobApplicationDetails details)
  {
    return new JobApplication(id, owner, details);
  }

  public JobApplication updateTracking(LocalDate applicationDate, ResponseStatus responseStatus, String notes)
  {
    return new JobApplication(id, owner, new JobApplicationDetails(offerId, applicationDate, responseStatus, notes));
  }

  public boolean isOwnedBy(Username username)
  {
    return owner.equals(username);
  }

  public JobApplicationId id()
  {
    return id;
  }

  public Username owner()
  {
    return owner;
  }

  public OfferId offerId()
  {
    return offerId;
  }

  public LocalDate applicationDate()
  {
    return applicationDate;
  }

  public ResponseStatus responseStatus()
  {
    return responseStatus;
  }

  public String notes()
  {
    return notes;
  }
}
