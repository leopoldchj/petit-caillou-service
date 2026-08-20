package com.petitcaillou.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.application.dto.JobApplicationRequest;
import com.petitcaillou.application.dto.JobApplicationView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.JobApplicationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobApplicationResourceTest
{
  private static final Username OWNER = Username.of("owner");
  private static final AuthenticatedUser CURRENT = new AuthenticatedUser(OWNER, Email.of("owner@app.com"), Role.USER);
  private static final UUID ID = UUID.randomUUID();
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final JobApplicationRequest REQUEST = new JobApplicationRequest(
    "https://jobs.example.com/1", COMPANY.value().toString(), "Backend engineer", "A role", "Paris",
    LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW);

  private final JobApplicationService service = mock(JobApplicationService.class);
  private final CompanyService companyService = mock(CompanyService.class);
  private final JobApplicationResource resource = new JobApplicationResource(service, companyService);

  private JobApplication domainApplication()
  {
    JobApplicationDetails details = new JobApplicationDetails(
      "https://jobs.example.com/1", COMPANY, "Backend engineer", "A role", "Paris", LocalDate.of(2026, 1, 15),
      ResponseStatus.INTERVIEW);
    return JobApplication.reconstitute(JobApplicationId.of(ID), OWNER, details);
  }

  private Company company()
  {
    return Company.reconstitute(COMPANY, "ACME", "https://acme.example.com");
  }

  @Test
  void given_request_when_creating_then_returnsTheCreatedApplicationId()
  {
    when(service.create(eq(OWNER), any())).thenReturn(domainApplication());
    when(companyService.byId(COMPANY)).thenReturn(company());

    JobApplicationView view = resource.create(CURRENT, REQUEST);

    assertThat(view.id()).isEqualTo(ID.toString());
  }

  @Test
  void given_request_when_creating_then_embedsTheCompanyName()
  {
    when(service.create(eq(OWNER), any())).thenReturn(domainApplication());
    when(companyService.byId(COMPANY)).thenReturn(company());

    JobApplicationView view = resource.create(CURRENT, REQUEST);

    assertThat(view.company().name()).isEqualTo("ACME");
  }

  @Test
  void given_request_when_updating_then_returnsTheUpdatedTitle()
  {
    when(service.update(eq(OWNER), eq(JobApplicationId.of(ID)), any())).thenReturn(domainApplication());
    when(companyService.byId(COMPANY)).thenReturn(company());

    JobApplicationView view = resource.update(CURRENT, ID.toString(), REQUEST);

    assertThat(view.title()).isEqualTo("Backend engineer");
  }

  @Test
  void given_id_when_gettingById_then_returnsTheApplicationWithCompany()
  {
    when(service.byId(OWNER, JobApplicationId.of(ID))).thenReturn(domainApplication());
    when(companyService.byId(COMPANY)).thenReturn(company());

    JobApplicationView view = resource.get(CURRENT, ID.toString());

    assertThat(view.company().name()).isEqualTo("ACME");
  }

  @Test
  void given_owner_when_listing_then_returnsTheirApplications()
  {
    when(service.list(OWNER)).thenReturn(List.of(domainApplication()));
    when(companyService.all()).thenReturn(List.of(company()));

    List<JobApplicationView> views = resource.list(CURRENT, null);

    assertThat(views).hasSize(1);
  }

  @Test
  void given_companyFilter_when_listing_then_returnsFilteredApplications()
  {
    when(service.listByCompany(OWNER, COMPANY)).thenReturn(List.of(domainApplication()));
    when(companyService.all()).thenReturn(List.of(company()));

    List<JobApplicationView> views = resource.list(CURRENT, COMPANY.value().toString());

    assertThat(views).hasSize(1);
  }

  @Test
  void given_id_when_deleting_then_delegatesToServiceWithCurrentUser()
  {
    resource.delete(CURRENT, ID.toString());

    verify(service).delete(OWNER, JobApplicationId.of(ID));
  }
}
