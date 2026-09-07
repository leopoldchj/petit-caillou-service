package com.petitcaillou.application;

import java.util.UUID;

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

import com.petitcaillou.application.dto.CompanyRequest;
import com.petitcaillou.application.dto.CompanyView;
import com.petitcaillou.application.dto.PageView;
import com.petitcaillou.domain.company.CompanyId;
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
  public PageView<CompanyView> list(
    @RequestParam(name = "query", required = false) String query,
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size)
  {
    return PageView.from(companyService.search(query, PageParams.of(page, size)), CompanyView::from);
  }

  @GetMapping("/{id}")
  public CompanyView get(@PathVariable String id)
  {
    return CompanyView.from(companyService.byId(companyId(id)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CompanyView create(@Valid @RequestBody CompanyRequest request)
  {
    return CompanyView.from(companyService.create(request.name(), request.website()));
  }

  @PutMapping("/{id}")
  public CompanyView update(@PathVariable String id, @Valid @RequestBody CompanyRequest request)
  {
    return CompanyView.from(companyService.update(companyId(id), request.name(), request.website()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id)
  {
    companyService.delete(companyId(id));
  }

  private CompanyId companyId(String id)
  {
    return CompanyId.of(UUID.fromString(id));
  }
}
