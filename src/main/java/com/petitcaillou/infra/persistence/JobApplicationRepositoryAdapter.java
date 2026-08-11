package com.petitcaillou.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.user.Username;

@Component
public class JobApplicationRepositoryAdapter implements JobApplicationRepository
{
  private final SpringDataJobApplicationRepository jpa;

  public JobApplicationRepositoryAdapter(SpringDataJobApplicationRepository jpa)
  {
    this.jpa = jpa;
  }

  @Override
  public JobApplication save(JobApplication application)
  {
    JobApplicationEntity saved = jpa.save(JobApplicationMapper.toEntity(application));
    return JobApplicationMapper.toDomain(saved);
  }

  @Override
  public Optional<JobApplication> findById(JobApplicationId id)
  {
    return jpa.findById(id.value().toString()).map(JobApplicationMapper::toDomain);
  }

  @Override
  public List<JobApplication> findByOwner(Username owner)
  {
    return jpa.findByOwnerUsername(owner.value()).stream().map(JobApplicationMapper::toDomain).toList();
  }

  @Override
  public List<JobApplication> findByOwnerAndCompany(Username owner, CompanyId companyId)
  {
    return jpa.findByOwnerUsernameAndCompanyId(owner.value(), companyId.value().toString())
      .stream().map(JobApplicationMapper::toDomain).toList();
  }

  @Override
  public void deleteById(JobApplicationId id)
  {
    jpa.deleteById(id.value().toString());
  }
}
