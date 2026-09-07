package com.petitcaillou.domain.jobapplication;

import java.util.Optional;

import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

public interface JobApplicationRepository
{
  JobApplication save(JobApplication application);

  Optional<JobApplication> findById(JobApplicationId id);

  Page<JobApplication> findByOwner(Username owner, PageRequest pageRequest);

  boolean existsByOwnerAndOffer(Username owner, OfferId offerId);

  void deleteById(JobApplicationId id);
}
