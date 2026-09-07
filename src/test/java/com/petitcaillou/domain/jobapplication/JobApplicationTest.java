package com.petitcaillou.domain.jobapplication;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JobApplicationTest
{
  private static final Username OWNER = Username.of("owner");
  private static final OfferId OFFER = OfferId.of(UUID.randomUUID());

  private JobApplicationDetails details(ResponseStatus status)
  {
    return new JobApplicationDetails(OFFER, LocalDate.of(2026, 1, 15), status, "note");
  }

  @Test
  void given_details_when_created_then_linksToTheOffer()
  {
    JobApplication application = JobApplication.create(OWNER, details(ResponseStatus.INTERVIEW));

    assertThat(application.offerId()).isEqualTo(OFFER);
  }

  @Test
  void given_nullStatus_when_created_then_defaultsToNoResponse()
  {
    JobApplication application = JobApplication.create(OWNER, details(null));

    assertThat(application.responseStatus()).isEqualTo(ResponseStatus.NO_RESPONSE);
  }

  @Test
  void given_missingOffer_when_created_then_throws()
  {
    JobApplicationDetails invalid = new JobApplicationDetails(null, null, ResponseStatus.NO_RESPONSE, null);

    assertThatThrownBy(() -> JobApplication.create(OWNER, invalid))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_owner_when_checkingOwnership_then_recognisesTheOwner()
  {
    JobApplication application = JobApplication.create(OWNER, details(ResponseStatus.NO_RESPONSE));

    assertThat(application.isOwnedBy(OWNER)).isTrue();
  }

  @Test
  void given_application_when_updatingTracking_then_changesTheStatus()
  {
    JobApplication application = JobApplication.create(OWNER, details(ResponseStatus.NO_RESPONSE));

    JobApplication updated = application.updateTracking(LocalDate.of(2026, 2, 1), ResponseStatus.ACCEPTED, "great");

    assertThat(updated.responseStatus()).isEqualTo(ResponseStatus.ACCEPTED);
  }

  @Test
  void given_application_when_updatingTracking_then_keepsTheSameOffer()
  {
    JobApplication application = JobApplication.create(OWNER, details(ResponseStatus.NO_RESPONSE));

    JobApplication updated = application.updateTracking(LocalDate.of(2026, 2, 1), ResponseStatus.ACCEPTED, "great");

    assertThat(updated.offerId()).isEqualTo(OFFER);
  }
}
