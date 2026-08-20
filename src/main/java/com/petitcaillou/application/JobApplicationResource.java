package com.petitcaillou.application;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.petitcaillou.application.dto.JobApplicationRequest;
import com.petitcaillou.application.dto.JobApplicationView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.JobApplicationService;

@RestController
@RequestMapping("/applications")
public class JobApplicationResource
{
  private final JobApplicationService jobApplicationService;
  private final CompanyService companyService;

  public JobApplicationResource(JobApplicationService jobApplicationService, CompanyService companyService)
  {
    this.jobApplicationService = jobApplicationService;
    this.companyService = companyService;
  }

  @GetMapping
  public List<JobApplicationView> list(@CurrentUser AuthenticatedUser current,
    @RequestParam(name = "companyId", required = false) String companyId)
  {
    List<JobApplication> applications = companyId == null
      ? jobApplicationService.list(current.username())
      : jobApplicationService.listByCompany(current.username(), company(companyId));

    Map<String, Company> byId = companyService.all().stream()
      .collect(Collectors.toMap(each -> each.id().value().toString(), Function.identity()));

    return applications.stream()
      .map(application -> JobApplicationView.from(application, byId.get(application.companyId().value().toString())))
      .toList();
  }

  @GetMapping("/{id}")
  public JobApplicationView get(@CurrentUser AuthenticatedUser current, @PathVariable String id)
  {
    return view(jobApplicationService.byId(current.username(), identifier(id)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public JobApplicationView create(@CurrentUser AuthenticatedUser current,
    @Valid @RequestBody JobApplicationRequest request)
  {
    return view(jobApplicationService.create(current.username(), toDetails(request)));
  }

  @PutMapping("/{id}")
  public JobApplicationView update(@CurrentUser AuthenticatedUser current, @PathVariable String id,
    @Valid @RequestBody JobApplicationRequest request)
  {
    return view(jobApplicationService.update(current.username(), identifier(id), toDetails(request)));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@CurrentUser AuthenticatedUser current, @PathVariable String id)
  {
    jobApplicationService.delete(current.username(), identifier(id));
  }

  private JobApplicationView view(JobApplication application)
  {
    return JobApplicationView.from(application, companyService.byId(application.companyId()));
  }

  private JobApplicationDetails toDetails(JobApplicationRequest request)
  {
    return new JobApplicationDetails(
      request.link(),
      company(request.companyId()),
      request.title(),
      request.description(),
      request.location(),
      request.applicationDate(),
      request.responseStatus());
  }

  private CompanyId company(String companyId)
  {
    return CompanyId.of(UUID.fromString(companyId));
  }

  private JobApplicationId identifier(String id)
  {
    return JobApplicationId.of(UUID.fromString(id));
  }
}
