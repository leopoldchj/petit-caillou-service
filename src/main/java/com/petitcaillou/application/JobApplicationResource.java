package com.petitcaillou.application;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.petitcaillou.application.dto.JobApplicationRequest;
import com.petitcaillou.application.dto.JobApplicationUpdateRequest;
import com.petitcaillou.application.dto.JobApplicationView;
import com.petitcaillou.application.dto.PageView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.jobapplication.JobApplication;
import com.petitcaillou.domain.jobapplication.JobApplicationDetails;
import com.petitcaillou.domain.jobapplication.JobApplicationId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.JobApplicationService;
import com.petitcaillou.service.OfferService;

@RestController
@RequestMapping("/applications")
public class JobApplicationResource
{
  private final JobApplicationService jobApplicationService;
  private final OfferService offerService;
  private final CompanyService companyService;

  public JobApplicationResource(JobApplicationService jobApplicationService, OfferService offerService,
    CompanyService companyService)
  {
    this.jobApplicationService = jobApplicationService;
    this.offerService = offerService;
    this.companyService = companyService;
  }

  @GetMapping
  public PageView<JobApplicationView> list(@CurrentUser AuthenticatedUser current,
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size)
  {
    return PageView.from(
      jobApplicationService.list(current.username(), PageParams.of(page, size)),
      application -> view(current, application));
  }

  @GetMapping("/{id}")
  public JobApplicationView get(@CurrentUser AuthenticatedUser current, @PathVariable String id)
  {
    return view(current, jobApplicationService.byId(current.username(), identifier(id)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public JobApplicationView apply(@CurrentUser AuthenticatedUser current,
    @Valid @RequestBody JobApplicationRequest request)
  {
    JobApplicationDetails details = new JobApplicationDetails(
      OfferId.of(UUID.fromString(request.offerId())),
      request.applicationDate(),
      request.responseStatus(),
      request.notes());
    return view(current, jobApplicationService.apply(current.username(), details));
  }

  @PatchMapping("/{id}")
  public JobApplicationView update(@CurrentUser AuthenticatedUser current, @PathVariable String id,
    @Valid @RequestBody JobApplicationUpdateRequest request)
  {
    JobApplication updated = jobApplicationService.updateTracking(
      current.username(), identifier(id), request.applicationDate(), request.responseStatus(), request.notes());
    return view(current, updated);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@CurrentUser AuthenticatedUser current, @PathVariable String id)
  {
    jobApplicationService.delete(current.username(), identifier(id));
  }

  private JobApplicationView view(AuthenticatedUser current, JobApplication application)
  {
    Offer offer = offerService.byId(current.username(), application.offerId());
    return JobApplicationView.from(application, offer, companyService.byId(offer.companyId()));
  }

  private JobApplicationId identifier(String id)
  {
    return JobApplicationId.of(UUID.fromString(id));
  }
}
