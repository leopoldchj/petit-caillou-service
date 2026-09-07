package com.petitcaillou.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.NormalizedName;
import com.petitcaillou.domain.pagination.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyRepositoryAdapterTest
{
  private static final UUID ID = UUID.randomUUID();

  private final SpringDataCompanyRepository jpa = mock(SpringDataCompanyRepository.class);
  private final CompanyRepositoryAdapter adapter = new CompanyRepositoryAdapter(jpa);

  private CompanyEntity storedEntity()
  {
    return new CompanyEntity(ID.toString(), "ACME", "acme", "https://acme.example.com");
  }

  @Test
  void given_company_when_saving_then_returnsMappedSavedCompany()
  {
    when(jpa.save(any(CompanyEntity.class))).thenReturn(storedEntity());

    assertThat(adapter.save(Company.reconstitute(CompanyId.of(ID), "ACME", null)).id().value()).isEqualTo(ID);
  }

  @Test
  void given_storedEntity_when_findingById_then_returnsMappedCompany()
  {
    when(jpa.findById(ID.toString())).thenReturn(Optional.of(storedEntity()));

    assertThat(adapter.findById(CompanyId.of(ID)).orElseThrow().name()).isEqualTo("ACME");
  }

  @Test
  void given_storedEntity_when_findingByNormalizedName_then_returnsMappedCompany()
  {
    when(jpa.findByNormalizedName("acme")).thenReturn(Optional.of(storedEntity()));

    assertThat(adapter.findByNormalizedName(NormalizedName.of("ACME")).orElseThrow().id().value()).isEqualTo(ID);
  }

  @Test
  void given_ids_when_findingAllByIds_then_returnsMappedCompanies()
  {
    when(jpa.findAllById(anyCollection())).thenReturn(List.of(storedEntity()));

    assertThat(adapter.findAllByIds(List.of(CompanyId.of(ID)))).hasSize(1);
  }

  @Test
  void given_id_when_checkingExistence_then_delegatesToJpa()
  {
    when(jpa.existsById(ID.toString())).thenReturn(true);

    assertThat(adapter.existsById(CompanyId.of(ID))).isTrue();
  }

  @Test
  void given_prefix_when_searching_then_returnsMappedPage()
  {
    when(jpa.findByNormalizedNameStartingWith(eq("ac"), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(storedEntity())));

    assertThat(adapter.search("ac", new PageRequest(0, 20)).items()).hasSize(1);
  }

  @Test
  void given_id_when_deleting_then_delegatesToJpa()
  {
    adapter.delete(CompanyId.of(ID));

    verify(jpa).deleteById(ID.toString());
  }
}
