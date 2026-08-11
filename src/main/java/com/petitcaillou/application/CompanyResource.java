package com.petitcaillou.application;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.petitcaillou.application.dto.CompanyRequest;
import com.petitcaillou.application.dto.CompanyView;
import com.petitcaillou.service.CompanyService;

@RestController
@RequestMapping("/companies")
public class CompanyResource
{
  private final CompanyService companyService;

  public CompanyResource(CompanyService companyService)
  {
    this.companyService = companyService;
  }

  @GetMapping
  public List<CompanyView> list()
  {
    return companyService.all().stream().map(CompanyView::from).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CompanyView create(@Valid @RequestBody CompanyRequest request)
  {
    return CompanyView.from(companyService.create(request.name(), request.website()));
  }
}
