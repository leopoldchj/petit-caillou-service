package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobApplicationRepositoryAdapterTest
{
  private static final UUID ID = UUID.randomUUID();
  private static final UUID COMPANY = UUID.randomUUID();
  private static final JobApplicationDetails DETAILS = new JobApplicationDetails(
    "https://jobs.example.com/1", CompanyId.of(COMPANY), "Backend engineer", "A role", LocalDate.of(2026, 1, 15),
    ResponseStatus.INTERVIEW);

  private final SpringDataJobApplicationRepository jpa = mock(SpringDataJobApplicationRepository.class);
  private final JobApplicationRepositoryAdapter adapter = new JobApplicationRepositoryAdapter(jpa);

  private JobApplicationEntity storedEntity()
  {
    return new JobApplicationEntity(
      ID.toString(), "owner", COMPANY.toString(), "https://jobs.example.com/1", "Backend engineer", "A role",
      LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW);
  }

  @Test
  void given_application_when_saving_then_returnsMappedSavedApplication()
  {
    JobApplication application = JobApplication.reconstitute(
      JobApplicationId.of(ID), Username.of("owner"), DETAILS);
    when(jpa.save(any(JobApplicationEntity.class))).thenReturn(storedEntity());

    JobApplication saved = adapter.save(application);

    assertThat(saved.id().value()).isEqualTo(ID);
  }

  @Test
  void given_storedEntity_when_findingById_then_returnsMappedApplication()
  {
    when(jpa.findById(ID.toString())).thenReturn(Optional.of(storedEntity()));

    Optional<JobApplication> found = adapter.findById(JobApplicationId.of(ID));

    assertThat(found.orElseThrow().id().value()).isEqualTo(ID);
  }

  @Test
  void given_missingId_when_findingById_then_returnsEmpty()
  {
    when(jpa.findById(ID.toString())).thenReturn(Optional.empty());

    Optional<JobApplication> found = adapter.findById(JobApplicationId.of(ID));

    assertThat(found).isEmpty();
  }

  @Test
  void given_ownerApplications_when_findingByOwner_then_returnsMappedApplications()
  {
    when(jpa.findByOwnerUsername("owner")).thenReturn(List.of(storedEntity()));

    assertThat(adapter.findByOwner(Username.of("owner"))).hasSize(1);
  }

  @Test
  void given_ownerAndCompany_when_findingByOwnerAndCompany_then_returnsMappedApplications()
  {
    when(jpa.findByOwnerUsernameAndCompanyId("owner", COMPANY.toString())).thenReturn(List.of(storedEntity()));

    assertThat(adapter.findByOwnerAndCompany(Username.of("owner"), CompanyId.of(COMPANY))).hasSize(1);
  }

  @Test
  void given_id_when_deleting_then_delegatesToJpa()
  {
    adapter.deleteById(JobApplicationId.of(ID));

    verify(jpa).deleteById(ID.toString());
  }
}
