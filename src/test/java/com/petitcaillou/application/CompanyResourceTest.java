package com.petitcaillou.application;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.application.dto.CompanyRequest;
import com.petitcaillou.application.dto.CompanyView;
import com.petitcaillou.application.dto.PageView;
import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.service.CompanyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyResourceTest
{
  private static final CompanyId ID = CompanyId.of(UUID.randomUUID());

  private final CompanyService companyService = mock(CompanyService.class);
  private final CompanyResource resource = new CompanyResource(companyService);

  @Test
  void given_query_when_listing_then_returnsAPageOfCompanies()
  {
    when(companyService.search(any(), any()))
      .thenReturn(new Page<>(List.of(Company.reconstitute(ID, "ACME", null)), 0, 20, 1));

    PageView<CompanyView> page = resource.list("ac", null, null);

    assertThat(page.items()).hasSize(1);
  }

  @Test
  void given_request_when_creating_then_returnsTheCreatedCompany()
  {
    when(companyService.create("ACME", null)).thenReturn(Company.reconstitute(ID, "ACME", null));

    assertThat(resource.create(new CompanyRequest("ACME", null)).name()).isEqualTo("ACME");
  }

  @Test
  void given_request_when_updating_then_returnsTheUpdatedCompany()
  {
    when(companyService.update(ID, "ACME", null)).thenReturn(Company.reconstitute(ID, "ACME", null));

    assertThat(resource.update(ID.value().toString(), new CompanyRequest("ACME", null)).id()).isEqualTo(ID.value().toString());
  }

  @Test
  void given_id_when_deleting_then_delegatesToService()
  {
    resource.delete(ID.value().toString());

    verify(companyService).delete(ID);
  }
}
