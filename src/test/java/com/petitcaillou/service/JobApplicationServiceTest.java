package com.petitcaillou.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationAlreadyExistsException;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationNotFoundException;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.exceptions.OfferNotFoundException;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
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
  private static final OfferId OFFER = OfferId.of(UUID.randomUUID());
  private static final JobApplicationDetails DETAILS =
    new JobApplicationDetails(OFFER, LocalDate.of(2026, 1, 15), ResponseStatus.NO_RESPONSE, "note");

  private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
  private final OfferRepository offers = mock(OfferRepository.class);
  private final JobApplicationService service = new JobApplicationService(applications, offers);

  private Offer offerOwnedBy(Username creator)
  {
    OfferDetails details =
      new OfferDetails(CompanyId.of(UUID.randomUUID()), "Backend", "Paris", LocalDate.of(2026, 1, 1), null, null);
    return Offer.reconstitute(OFFER, creator, details, true);
  }

  private JobApplication ownedBy(Username owner)
  {
    return JobApplication.reconstitute(ID, owner, DETAILS);
  }

  @Test
  void given_visibleOffer_when_applying_then_saves()
  {
    when(offers.findById(OFFER)).thenReturn(Optional.of(offerOwnedBy(OWNER)));
    when(applications.existsByOwnerAndOffer(OWNER, OFFER)).thenReturn(false);
    when(applications.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.apply(OWNER, DETAILS);

    verify(applications).save(any(JobApplication.class));
  }

  @Test
  void given_offerNotVisible_when_applying_then_throwsOfferNotFound()
  {
    when(offers.findById(OFFER)).thenReturn(Optional.of(offerOwnedBy(OTHER)));

    assertThatThrownBy(() -> service.apply(OWNER, DETAILS))
      .isInstanceOf(OfferNotFoundException.class);
  }

  @Test
  void given_missingOffer_when_applying_then_throwsOfferNotFound()
  {
    when(offers.findById(OFFER)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.apply(OWNER, DETAILS))
      .isInstanceOf(OfferNotFoundException.class);
  }

  @Test
  void given_alreadyApplied_when_applying_then_throwsAlreadyExists()
  {
    when(offers.findById(OFFER)).thenReturn(Optional.of(offerOwnedBy(OWNER)));
    when(applications.existsByOwnerAndOffer(OWNER, OFFER)).thenReturn(true);

    assertThatThrownBy(() -> service.apply(OWNER, DETAILS))
      .isInstanceOf(JobApplicationAlreadyExistsException.class);
  }

  @Test
  void given_alreadyApplied_when_applying_then_neverSaves()
  {
    when(offers.findById(OFFER)).thenReturn(Optional.of(offerOwnedBy(OWNER)));
    when(applications.existsByOwnerAndOffer(OWNER, OFFER)).thenReturn(true);
    catchThrowable(() -> service.apply(OWNER, DETAILS));

    verify(applications, never()).save(any());
  }

  @Test
  void given_ownedApplication_when_updatingTracking_then_saves()
  {
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OWNER)));
    when(applications.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateTracking(OWNER, ID, LocalDate.of(2026, 2, 1), ResponseStatus.ACCEPTED, "great");

    verify(applications).save(any(JobApplication.class));
  }

  @Test
  void given_applicationOwnedByAnother_when_updatingTracking_then_throwsNotFound()
  {
    when(applications.findById(ID)).thenReturn(Optional.of(ownedBy(OTHER)));

    assertThatThrownBy(() -> service.updateTracking(OWNER, ID, null, ResponseStatus.ACCEPTED, null))
      .isInstanceOf(JobApplicationNotFoundException.class);
  }

  @Test
  void given_missingApplication_when_updatingTracking_then_throwsNotFound()
  {
    when(applications.findById(ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateTracking(OWNER, ID, null, ResponseStatus.ACCEPTED, null))
      .isInstanceOf(JobApplicationNotFoundException.class);
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
  void given_owner_when_listing_then_returnsTheirPage()
  {
    PageRequest pageRequest = new PageRequest(0, 20);
    when(applications.findByOwner(OWNER, pageRequest))
      .thenReturn(new Page<>(List.of(ownedBy(OWNER)), 0, 20, 1));

    assertThat(service.list(OWNER, pageRequest).totalElements()).isEqualTo(1);
  }
}
