package com.petitcaillou.service;

import java.util.List;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationNotFoundException;
import com.petitcaillou.domain.user.Username;

public class JobApplicationService
{
  private final JobApplicationRepository applications;
  private final CompanyRepository companies;

  public JobApplicationService(JobApplicationRepository applications, CompanyRepository companies)
  {
    this.applications = applications;
    this.companies = companies;
  }

  public JobApplication create(Username owner, JobApplicationDetails details)
  {
    requireCompany(details.companyId());
    return applications.save(JobApplication.create(owner, details));
  }

  public JobApplication update(Username owner, JobApplicationId id, JobApplicationDetails details)
  {
    requireCompany(details.companyId());
    JobApplication application = ownedApplication(owner, id);
    return applications.save(application.update(details));
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

  public List<JobApplication> list(Username owner)
  {
    return applications.findByOwner(owner);
  }

  public List<JobApplication> listByCompany(Username owner, CompanyId companyId)
  {
    return applications.findByOwnerAndCompany(owner, companyId);
  }

  private void requireCompany(CompanyId companyId)
  {
    if (companyId == null || !companies.existsById(companyId))
    {
      throw new CompanyNotFoundException();
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
