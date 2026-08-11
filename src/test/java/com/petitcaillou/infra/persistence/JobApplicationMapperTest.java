package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;

class JobApplicationMapperTest
{
  private static final UUID ID = UUID.randomUUID();
  private static final UUID COMPANY = UUID.randomUUID();
  private static final JobApplicationDetails DETAILS = new JobApplicationDetails(
    "https://jobs.example.com/1", CompanyId.of(COMPANY), "Backend engineer", "A role", LocalDate.of(2026, 1, 15),
    ResponseStatus.INTERVIEW);

  @Test
  void given_domainApplication_when_mappingToEntity_then_copiesTheCompanyId()
  {
    JobApplication application = JobApplication.reconstitute(
      JobApplicationId.of(ID), Username.of("owner"), DETAILS);

    JobApplicationEntity entity = JobApplicationMapper.toEntity(application);

    assertThat(entity.getCompanyId()).isEqualTo(COMPANY.toString());
  }

  @Test
  void given_entity_when_mappingToDomain_then_copiesTheTitle()
  {
    JobApplicationEntity entity = new JobApplicationEntity(
      ID.toString(), "owner", COMPANY.toString(), "https://jobs.example.com/1", "Backend engineer", "A role",
      LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW);

    JobApplication application = JobApplicationMapper.toDomain(entity);

    assertThat(application.title()).isEqualTo("Backend engineer");
  }
}
