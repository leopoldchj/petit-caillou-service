package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobApplicationRepositoryAdapterTest
{
  private static final UUID ID = UUID.randomUUID();
  private static final UUID OFFER = UUID.randomUUID();

  private final SpringDataJobApplicationRepository jpa = mock(SpringDataJobApplicationRepository.class);
  private final JobApplicationRepositoryAdapter adapter = new JobApplicationRepositoryAdapter(jpa);

  private JobApplicationEntity storedEntity()
  {
    return new JobApplicationEntity(
      ID.toString(), "owner", OFFER.toString(), LocalDate.of(2026, 1, 15), ResponseStatus.INTERVIEW, "note");
  }

  @Test
  void given_storedEntity_when_findingById_then_returnsMappedApplication()
  {
    when(jpa.findById(ID.toString())).thenReturn(Optional.of(storedEntity()));

    Optional<JobApplication> found = adapter.findById(JobApplicationId.of(ID));

    assertThat(found.orElseThrow().id().value()).isEqualTo(ID);
  }

  @Test
  void given_ownerApplications_when_findingByOwner_then_returnsMappedPage()
  {
    when(jpa.findByOwnerUsername(eq("owner"), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(storedEntity())));

    assertThat(adapter.findByOwner(Username.of("owner"), new PageRequest(0, 20)).items()).hasSize(1);
  }

  @Test
  void given_ownerAndOffer_when_checkingExistence_then_delegatesToJpa()
  {
    when(jpa.existsByOwnerUsernameAndOfferId("owner", OFFER.toString())).thenReturn(true);

    assertThat(adapter.existsByOwnerAndOffer(Username.of("owner"), OfferId.of(OFFER))).isTrue();
  }

  @Test
  void given_id_when_deleting_then_delegatesToJpa()
  {
    adapter.deleteById(JobApplicationId.of(ID));

    verify(jpa).deleteById(ID.toString());
  }
}
