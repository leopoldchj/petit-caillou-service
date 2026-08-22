package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationNotFoundException;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobApplicationServiceTest
{
  private static final Username OWNER = Username.of("owner");
  private static final Username OTHER = Username.of("other");
  private static final JobApplicationId ID = JobApplicationId.of(UUID.randomUUID());
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final JobApplicationDetails DETAILS = new JobApplicationDetails(
    "https://jobs.example.com/1", COMPANY, "Backend engineer", "A role", "Paris", LocalDate.of(2026, 1, 15),
    ResponseStatus.NO_RESPONSE);

  private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
  private final CompanyRepository companies = mock(CompanyRepository.class);
  private final JobApplicationService service = new JobApplicationService(applications, companies);

  private JobApplication ownedBy(Username owner)
  {
    return JobApplication.reconstitute(ID, owner, DETAILS);
  }

  @Test
  void given_knownCompany_when_creating_then_savesTheApplication()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(applications.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.create(OWNER, DETAILS);

    verify(applications).save(any(JobApplication.class));
  }

  @Test
  void given_knownCompany_when_creating_then_theApplicationIsOwnedByThatOwner()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(applications.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    JobApplication created = service.create(OWNER, DETAILS);

    assertThat(created.isOwnedBy(OWNER)).isTrue();
  }

  @Test
  void given_unknownCompany_when_creating_then_throwsCompanyNotFound()
  {
    when(companies.existsById(COMPANY)).thenReturn(false);

    assertThatThrownBy(() -> service.create(OWNER, DETAILS))
      .isInstanceOf(CompanyNotFoundException.class);
  }

  @Test
  void given_unknownCompany_when_creating_then_neverSaves()
  {
    when(companies.existsById(COMPANY)).thenReturn(false);
    catchThrowable(() -> service.create(OWNER, DETAILS));

    verify(applications, never()).save(any());
  }

  @Test
  void given_ownedApplication_when_updating_then_savesTheUpdatedApplication()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OWNER)));
    when(applications.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.update(OWNER, ID, DETAILS);

    verify(applications).save(any(JobApplication.class));
  }

  @Test
  void given_unknownCompany_when_updating_then_throwsCompanyNotFound()
  {
    when(companies.existsById(COMPANY)).thenReturn(false);

    assertThatThrownBy(() -> service.update(OWNER, ID, DETAILS))
      .isInstanceOf(CompanyNotFoundException.class);
  }

  @Test
  void given_missingApplication_when_updating_then_throwsNotFound()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(applications.findById(ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(OWNER, ID, DETAILS))
      .isInstanceOf(JobApplicationNotFoundException.class);
  }

  @Test
  void given_applicationOwnedByAnother_when_updating_then_throwsNotFound()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OTHER)));

    assertThatThrownBy(() -> service.update(OWNER, ID, DETAILS))
      .isInstanceOf(JobApplicationNotFoundException.class);
  }

  @Test
  void given_applicationOwnedByAnother_when_updating_then_neverSaves()
  {
    when(companies.existsById(COMPANY)).thenReturn(true);
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OTHER)));
    catchThrowable(() -> service.update(OWNER, ID, DETAILS));

    verify(applications, never()).save(any());
  }

  @Test
  void given_ownedApplication_when_deleting_then_deletesById()
  {
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OWNER)));

    service.delete(OWNER, ID);

    verify(applications).deleteById(ID);
  }

  @Test
  void given_applicationOwnedByAnother_when_deleting_then_neverDeletes()
  {
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OTHER)));
    catchThrowable(() -> service.delete(OWNER, ID));

    verify(applications, never()).deleteById(any());
  }

  @Test
  void given_ownedApplication_when_gettingById_then_returnsIt()
  {
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OWNER)));

    assertThat(service.byId(OWNER, ID).isOwnedBy(OWNER)).isTrue();
  }

  @Test
  void given_applicationOwnedByAnother_when_gettingById_then_throwsNotFound()
  {
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OTHER)));

    assertThatThrownBy(() -> service.byId(OWNER, ID))
      .isInstanceOf(JobApplicationNotFoundException.class);
  }

  @Test
  void given_owner_when_listing_then_returnsOwnerApplications()
  {
    when(applications.findByOwner(OWNER)).thenReturn(List.of(ownedBy(OWNER)));

    assertThat(service.list(OWNER)).hasSize(1);
  }

  @Test
  void given_ownerAndCompany_when_listingByCompany_then_returnsFilteredApplications()
  {
    when(applications.findByOwnerAndCompany(OWNER, COMPANY)).thenReturn(List.of(ownedBy(OWNER)));

    assertThat(service.listByCompany(OWNER, COMPANY)).hasSize(1);
  }
}
