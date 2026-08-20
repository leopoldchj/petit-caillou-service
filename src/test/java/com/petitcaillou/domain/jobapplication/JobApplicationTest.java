package com.petitcaillou.domain.jobapplication;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JobApplicationTest
{
  private static final Username OWNER = Username.of("john_doe");
  private static final Username OTHER = Username.of("jane_doe");
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final JobApplicationDetails DETAILS = new JobApplicationDetails(
    "https://jobs.example.com/1", COMPANY, "Backend engineer", "A role", "Paris", LocalDate.of(2026, 1, 15), null);

  @Test
  void given_nullCompany_when_creating_then_rejectsIt()
  {
    JobApplicationDetails details = new JobApplicationDetails(null, null, "Backend engineer", null, null, null, null);

    assertThatThrownBy(() -> JobApplication.create(OWNER, details))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_nullTitle_when_creating_then_rejectsIt()
  {
    JobApplicationDetails details = new JobApplicationDetails(null, COMPANY, null, null, null, null, null);

    assertThatThrownBy(() -> JobApplication.create(OWNER, details))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_blankTitle_when_creating_then_rejectsIt()
  {
    JobApplicationDetails details = new JobApplicationDetails(null, COMPANY, "   ", null, null, null, null);

    assertThatThrownBy(() -> JobApplication.create(OWNER, details))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_nullResponseStatus_when_creating_then_defaultsToNoResponse()
  {
    JobApplication application = JobApplication.create(OWNER, DETAILS);

    assertThat(application.responseStatus()).isEqualTo(ResponseStatus.NO_RESPONSE);
  }

  @Test
  void given_detailsWithLocation_when_creating_then_exposesTheLocation()
  {
    JobApplication application = JobApplication.create(OWNER, DETAILS);

    assertThat(application.location()).isEqualTo("Paris");
  }

  @Test
  void given_ownerAndDetails_when_creating_then_isOwnedByThatOwner()
  {
    JobApplication application = JobApplication.create(OWNER, DETAILS);

    assertThat(application.isOwnedBy(OWNER)).isTrue();
  }

  @Test
  void given_anotherUsername_when_checkingOwnership_then_returnsFalse()
  {
    JobApplication application = JobApplication.create(OWNER, DETAILS);

    assertThat(application.isOwnedBy(OTHER)).isFalse();
  }

  @Test
  void given_newDetails_when_updating_then_keepsTheSameId()
  {
    JobApplication application = JobApplication.create(OWNER, DETAILS);

    JobApplication updated = application.update(new JobApplicationDetails(
      null, COMPANY, "Frontend engineer", null, null, null, ResponseStatus.INTERVIEW));

    assertThat(updated.id()).isEqualTo(application.id());
  }

  @Test
  void given_newDetails_when_updating_then_appliesTheNewTitle()
  {
    JobApplication application = JobApplication.create(OWNER, DETAILS);

    JobApplication updated = application.update(new JobApplicationDetails(
      null, COMPANY, "Frontend engineer", null, null, null, ResponseStatus.INTERVIEW));

    assertThat(updated.title()).isEqualTo("Frontend engineer");
  }
}
