package com.petitcaillou.application;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.application.dto.CompanyRequest;
import com.petitcaillou.application.dto.CompanyView;
import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.service.CompanyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyResourceTest
{
  private static final UUID ID = UUID.randomUUID();

  private final CompanyService companyService = mock(CompanyService.class);
  private final CompanyResource resource = new CompanyResource(companyService);

  @Test
  void given_storedCompanies_when_listing_then_returnsThem()
  {
    when(companyService.all()).thenReturn(List.of(Company.create("ACME", null)));

    List<CompanyView> views = resource.list();

    assertThat(views).hasSize(1);
  }

  @Test
  void given_request_when_creating_then_returnsTheCreatedName()
  {
    when(companyService.create("ACME", "https://acme.example.com"))
      .thenReturn(Company.create("ACME", "https://acme.example.com"));

    CompanyView view = resource.create(new CompanyRequest("ACME", "https://acme.example.com"));

    assertThat(view.name()).isEqualTo("ACME");
  }

  @Test
  void given_request_when_updating_then_returnsTheUpdatedName()
  {
    when(companyService.update(eq(CompanyId.of(ID)), eq("New name"), eq(null)))
      .thenReturn(Company.reconstitute(CompanyId.of(ID), "New name", null));

    CompanyView view = resource.update(ID.toString(), new CompanyRequest("New name", null));

    assertThat(view.name()).isEqualTo("New name");
  }

  @Test
  void given_id_when_deleting_then_delegatesToService()
  {
    resource.delete(ID.toString());

    verify(companyService).delete(CompanyId.of(ID));
  }
}
