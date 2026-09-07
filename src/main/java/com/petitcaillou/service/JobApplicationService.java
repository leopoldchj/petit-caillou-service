package com.petitcaillou.service;

import java.time.LocalDate;

import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.jobapplication.ResponseStatus;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationAlreadyExistsException;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationNotFoundException;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.exceptions.OfferNotFoundException;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

public class JobApplicationService
{
  private final JobApplicationRepository applications;
  private final OfferRepository offers;

  public JobApplicationService(JobApplicationRepository applications, OfferRepository offers)
  {
    this.applications = applications;
    this.offers = offers;
  }

  public JobApplication apply(Username owner, JobApplicationDetails details)
  {
    requireVisibleOffer(owner, details.offerId());

    if (applications.existsByOwnerAndOffer(owner, details.offerId()))
    {
      throw new JobApplicationAlreadyExistsException();
    }

    return applications.save(JobApplication.create(owner, details));
  }

  public JobApplication updateTracking(Username owner, JobApplicationId id, LocalDate applicationDate,
    ResponseStatus responseStatus, String notes)
  {
    JobApplication application = ownedApplication(owner, id);
    return applications.save(application.updateTracking(applicationDate, responseStatus, notes));
  }

  public void delete(Username owner, JobApplicationId id)
  {
    ownedApplication(owner, id);
    applications.deleteById(id);
  }

  public JobApplication byId(Username owner, JobApplicationId id)
  {
    return ownedApplication(owner, id);
  }

  public Page<JobApplication> list(Username owner, PageRequest pageRequest)
  {
    return applications.findByOwner(owner, pageRequest);
  }

  private void requireVisibleOffer(Username owner, OfferId offerId)
  {
    if (offerId == null || !offers.findById(offerId).map(offer -> offer.isVisibleTo(owner)).orElse(false))
    {
      throw new OfferNotFoundException();
    }
  }

  private JobApplication ownedApplication(Username owner, JobApplicationId id)
  {
    JobApplication application = applications.findById(id)
      .orElseThrow(JobApplicationNotFoundException::new);

    if (!application.isOwnedBy(owner))
    {
      throw new JobApplicationNotFoundException();
    }

    return application;
  }
}
