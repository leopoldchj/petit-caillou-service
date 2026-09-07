package com.petitcaillou.infra.persistence;

import java.util.UUID;

import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.offer.OfferId;
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
      application.offerId().value().toString(),
      application.applicationDate(),
      application.responseStatus(),
      application.notes());
  }

  static JobApplication toDomain(JobApplicationEntity entity)
  {
    JobApplicationDetails details = new JobApplicationDetails(
      OfferId.of(UUID.fromString(entity.getOfferId())),
      entity.getApplicationDate(),
      entity.getResponseStatus(),
      entity.getNotes());

    return JobApplication.reconstitute(
      JobApplicationId.of(UUID.fromString(entity.getId())),
      Username.of(entity.getOwnerUsername()),
      details);
  }
}
