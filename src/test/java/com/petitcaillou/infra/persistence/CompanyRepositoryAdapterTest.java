package com.petitcaillou.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompanyRepositoryAdapterTest
{
  private static final UUID ID = UUID.randomUUID();

  private final SpringDataCompanyRepository jpa = mock(SpringDataCompanyRepository.class);
  private final CompanyRepositoryAdapter adapter = new CompanyRepositoryAdapter(jpa);

  private CompanyEntity storedEntity()
  {
    return new CompanyEntity(ID.toString(), "ACME", "https://acme.example.com");
  }

  @Test
  void given_company_when_saving_then_returnsMappedSavedCompany()
  {
    when(jpa.save(any(CompanyEntity.class))).thenReturn(storedEntity());

    Company saved = adapter.save(Company.reconstitute(CompanyId.of(ID), "ACME", null));

    assertThat(saved.id().value()).isEqualTo(ID);
  }

  @Test
  void given_storedEntity_when_findingById_then_returnsMappedCompany()
  {
    when(jpa.findById(ID.toString())).thenReturn(Optional.of(storedEntity()));

    Optional<Company> found = adapter.findById(CompanyId.of(ID));

    assertThat(found.orElseThrow().name()).isEqualTo("ACME");
  }

  @Test
  void given_storedEntity_when_findingByName_then_returnsMappedCompany()
  {
    when(jpa.findByName("ACME")).thenReturn(Optional.of(storedEntity()));

    Optional<Company> found = adapter.findByName("ACME");

    assertThat(found.orElseThrow().id().value()).isEqualTo(ID);
  }

  @Test
  void given_id_when_checkingExistence_then_delegatesToJpa()
  {
    when(jpa.existsById(ID.toString())).thenReturn(true);

    assertThat(adapter.existsById(CompanyId.of(ID))).isTrue();
  }

  @Test
  void given_storedCompanies_when_findingAll_then_returnsMappedCompanies()
  {
    when(jpa.findAll()).thenReturn(List.of(storedEntity()));

    assertThat(adapter.findAll()).hasSize(1);
  }
}
