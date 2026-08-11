package com.petitcaillou.infra.persistence;

import java.util.UUID;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.user.Username;

final class JobApplicationMapper
{
  private JobApplicationMapper()
  {
  }

  static JobApplicationEntity toEntity(JobApplication application)
  {
    return new JobApplicationEntity(
      application.id().value().toString(),
      application.owner().value(),
      application.companyId().value().toString(),
      application.link(),
      application.title(),
      application.description(),
      application.applicationDate(),
      application.responseStatus());
  }

  static JobApplication toDomain(JobApplicationEntity entity)
  {
    JobApplicationDetails details = new JobApplicationDetails(
      entity.getLink(),
      CompanyId.of(UUID.fromString(entity.getCompanyId())),
      entity.getTitle(),
      entity.getDescription(),
      entity.getApplicationDate(),
      entity.getResponseStatus());

    return JobApplication.reconstitute(
      JobApplicationId.of(UUID.fromString(entity.getId())),
      Username.of(entity.getOwnerUsername()),
      details);
  }
}
