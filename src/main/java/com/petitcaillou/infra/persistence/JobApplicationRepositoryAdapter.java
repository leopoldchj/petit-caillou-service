package com.petitcaillou.infra.persistence;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

@Component
public class JobApplicationRepositoryAdapter implements JobApplicationRepository
{
  private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "applicationDate");

  private final SpringDataJobApplicationRepository jpa;

  public JobApplicationRepositoryAdapter(SpringDataJobApplicationRepository jpa)
  {
    this.jpa = jpa;
  }

  @Override
  public JobApplication save(JobApplication application)
  {
    return JobApplicationMapper.toDomain(jpa.save(JobApplicationMapper.toEntity(application)));
  }

  @Override
  public Optional<JobApplication> findById(JobApplicationId id)
  {
    return jpa.findById(id.value().toString()).map(JobApplicationMapper::toDomain);
  }

  @Override
  public Page<JobApplication> findByOwner(Username owner, PageRequest pageRequest)
  {
    org.springframework.data.domain.Page<JobApplicationEntity> page =
      jpa.findByOwnerUsername(owner.value(), PageMapper.toPageable(pageRequest, NEWEST_FIRST));
    return PageMapper.toDomain(page, pageRequest, JobApplicationMapper::toDomain);
  }

  @Override
  public boolean existsByOwnerAndOffer(Username owner, OfferId offerId)
  {
    return jpa.existsByOwnerUsernameAndOfferId(owner.value(), offerId.value().toString());
  }

  @Override
  public void deleteById(JobApplicationId id)
  {
    jpa.deleteById(id.value().toString());
  }
}
