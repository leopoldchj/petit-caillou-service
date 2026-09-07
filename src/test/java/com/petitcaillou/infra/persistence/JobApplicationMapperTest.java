package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;

class JobApplicationMapperTest
{
  private static final UUID ID = UUID.randomUUID();
  private static final UUID OFFER = UUID.randomUUID();
  private static final JobApplicationDetails DETAILS = new JobApplicationDetails(
    OfferId.of(OFFER), LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW, "note");

  @Test
  void given_domainApplication_when_mappingToEntity_then_copiesTheOfferId()
  {
    JobApplication application = JobApplication.reconstitute(JobApplicationId.of(ID), Username.of("owner"), DETAILS);

    JobApplicationEntity entity = JobApplicationMapper.toEntity(application);

    assertThat(entity.getOfferId()).isEqualTo(OFFER.toString());
  }

  @Test
  void given_entity_when_mappingToDomain_then_copiesTheStatus()
  {
    JobApplicationEntity entity = new JobApplicationEntity(
      ID.toString(), "owner", OFFER.toString(), LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW, "note");

    JobApplication application = JobApplicationMapper.toDomain(entity);

    assertThat(application.responseStatus()).isEqualTo(ResponseStatus.INTERVIEW);
  }
}
