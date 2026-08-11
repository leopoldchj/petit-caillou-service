package com.petitcaillou.domain.jobapplication;

import java.util.List;
import java.util.Optional;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.user.Username;

public interface JobApplicationRepository
{
  JobApplication save(JobApplication application);

  Optional<JobApplication> findById(JobApplicationId id);

  List<JobApplication> findByOwner(Username owner);

  List<JobApplication> findByOwnerAndCompany(Username owner, CompanyId companyId);

  void deleteById(JobApplicationId id);
}
