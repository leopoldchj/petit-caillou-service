package com.petitcaillou.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.application.dto.JobApplicationRequest;
import com.petitcaillou.application.dto.JobApplicationUpdateRequest;
import com.petitcaillou.application.dto.JobApplicationView;
import com.petitcaillou.application.dto.PageView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.JobApplicationService;
import com.petitcaillou.service.OfferService;

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
  private static final UUID OFFER = UUID.randomUUID();
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());

  private final JobApplicationService service = mock(JobApplicationService.class);
  private final OfferService offerService = mock(OfferService.class);
  private final CompanyService companyService = mock(CompanyService.class);
  private final JobApplicationResource resource = new JobApplicationResource(service, offerService, companyService);

  private JobApplication application()
  {
    JobApplicationDetails details =
      new JobApplicationDetails(OfferId.of(OFFER), LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW, "note");
    return JobApplication.reconstitute(JobApplicationId.of(ID), OWNER, details);
  }

  private Offer offer()
  {
    OfferDetails details =
      new OfferDetails(COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 1, 1), null, null);
    return Offer.reconstitute(OfferId.of(OFFER), OWNER, details, true);
  }

  private void stubView()
  {
    when(offerService.byId(eq(OWNER), any())).thenReturn(offer());
    when(companyService.byId(COMPANY)).thenReturn(Company.reconstitute(COMPANY, "ACME", null));
  }

  @Test
  void given_request_when_applying_then_returnsTheApplicationId()
  {
    when(service.apply(eq(OWNER), any())).thenReturn(application());
    stubView();

    JobApplicationView view = resource.apply(CURRENT,
      new JobApplicationRequest(OFFER.toString(), LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW, "note"));

    assertThat(view.id()).isEqualTo(ID.toString());
  }

  @Test
  void given_request_when_applying_then_embedsTheOfferTitle()
  {
    when(service.apply(eq(OWNER), any())).thenReturn(application());
    stubView();

    JobApplicationView view = resource.apply(CURRENT,
      new JobApplicationRequest(OFFER.toString(), null, null, null));

    assertThat(view.offer().title()).isEqualTo("Backend Engineer");
  }

  @Test
  void given_request_when_updating_then_returnsTheUpdatedApplication()
  {
    when(service.updateTracking(eq(OWNER), eq(JobApplicationId.of(ID)), any(), any(), any()))
      .thenReturn(application());
    stubView();

    JobApplicationView view = resource.update(CURRENT, ID.toString(),
      new JobApplicationUpdateRequest(LocalDate.of(2026, 2, 1), ResponseStatus.ACCEPTED, "great"));

    assertThat(view.id()).isEqualTo(ID.toString());
  }

  @Test
  void given_id_when_gettingById_then_returnsTheApplication()
  {
    when(service.byId(OWNER, JobApplicationId.of(ID))).thenReturn(application());
    stubView();

    assertThat(resource.get(CURRENT, ID.toString()).offer().company().name()).isEqualTo("ACME");
  }

  @Test
  void given_owner_when_listing_then_returnsAPage()
  {
    when(service.list(eq(OWNER), any())).thenReturn(new Page<>(List.of(application()), 0, 20, 1));
    stubView();

    PageView<JobApplicationView> page = resource.list(CURRENT, null, null);

    assertThat(page.items()).hasSize(1);
  }

  @Test
  void given_id_when_deleting_then_delegatesToServiceWithCurrentUser()
  {
    resource.delete(CURRENT, ID.toString());

    verify(service).delete(OWNER, JobApplicationId.of(ID));
  }
}
