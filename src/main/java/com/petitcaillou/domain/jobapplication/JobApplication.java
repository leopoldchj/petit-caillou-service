package com.petitcaillou.domain.jobapplication;

import java.time.LocalDate;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.user.Username;

public final class JobApplication
{
  private final JobApplicationId id;
  private final Username owner;
  private final CompanyId companyId;
  private final String link;
  private final String title;
  private final String description;
  private final LocalDate applicationDate;
  private final ResponseStatus responseStatus;

  private JobApplication(JobApplicationId id, Username owner, JobApplicationDetails details)
  {
    if (details.companyId() == null)
    {
      throw new IllegalArgumentException("Company is required");
    }
    if (details.title() == null || details.title().isBlank())
    {
      throw new IllegalArgumentException("Title is required");
    }

    this.id = id;
    this.owner = owner;
    this.companyId = details.companyId();
    this.link = details.link();
    this.title = details.title();
    this.description = details.description();
    this.applicationDate = details.applicationDate();
    this.responseStatus = details.responseStatus() == null ? ResponseStatus.NO_RESPONSE : details.responseStatus();
  }

  public static JobApplication create(Username owner, JobApplicationDetails details)
  {
    return new JobApplication(JobApplicationId.newId(), owner, details);
  }

  public static JobApplication reconstitute(JobApplicationId id, Username owner, JobApplicationDetails details)
  {
    return new JobApplication(id, owner, details);
  }

  public JobApplication update(JobApplicationDetails details)
  {
    return new JobApplication(id, owner, details);
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

  public CompanyId companyId()
  {
    return companyId;
  }

  public String link()
  {
    return link;
  }

  public String title()
  {
    return title;
  }

  public String description()
  {
    return description;
  }

  public LocalDate applicationDate()
  {
    return applicationDate;
  }

  public ResponseStatus responseStatus()
  {
    return responseStatus;
  }
}
