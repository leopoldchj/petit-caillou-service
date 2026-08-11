package com.petitcaillou.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyNameAlreadyUsedException;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyServiceTest
{
  private static final CompanyId EXISTING_ID = CompanyId.of(UUID.randomUUID());

  private final CompanyRepository companies = mock(CompanyRepository.class);
  private final CompanyService service = new CompanyService(companies);

  @Test
  void given_newName_when_creating_then_savesTheCompany()
  {
    when(companies.findByName("ACME")).thenReturn(Optional.empty());
    when(companies.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.create("ACME", "https://acme.example.com");

    verify(companies).save(any(Company.class));
  }

  @Test
  void given_existingName_when_creating_then_throwsWithTheExistingId()
  {
    when(companies.findByName("ACME")).thenReturn(Optional.of(Company.reconstitute(EXISTING_ID, "ACME", null)));

    assertThatExceptionOfType(CompanyNameAlreadyUsedException.class)
      .isThrownBy(() -> service.create("ACME", null))
      .satisfies(exception -> assertThat(exception.existingId()).isEqualTo(EXISTING_ID));
  }

  @Test
  void given_existingName_when_creating_then_neverSaves()
  {
    when(companies.findByName("ACME")).thenReturn(Optional.of(Company.reconstitute(EXISTING_ID, "ACME", null)));
    catchThrowable(() -> service.create("ACME", null));

    verify(companies, never()).save(any());
  }

  @Test
  void given_existingId_when_gettingById_then_returnsTheCompany()
  {
    when(companies.findById(EXISTING_ID)).thenReturn(Optional.of(Company.reconstitute(EXISTING_ID, "ACME", null)));

    assertThat(service.byId(EXISTING_ID).name()).isEqualTo("ACME");
  }

  @Test
  void given_missingId_when_gettingById_then_throwsCompanyNotFound()
  {
    when(companies.findById(EXISTING_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.byId(EXISTING_ID))
      .isInstanceOf(CompanyNotFoundException.class);
  }

  @Test
  void given_storedCompanies_when_listingAll_then_returnsThem()
  {
    when(companies.findAll()).thenReturn(List.of(Company.create("ACME", null)));

    assertThat(service.all()).hasSize(1);
  }
}
