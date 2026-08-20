package com.petitcaillou.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.company.exceptions.CompanyInUseException;
import com.petitcaillou.domain.company.exceptions.CompanyNameAlreadyUsedException;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;

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
  private static final CompanyId ID = CompanyId.of(UUID.randomUUID());
  private static final CompanyId OTHER_ID = CompanyId.of(UUID.randomUUID());

  private final CompanyRepository companies = mock(CompanyRepository.class);
  private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
  private final CompanyService service = new CompanyService(companies, applications);

  private Company company(CompanyId id, String name)
  {
    return Company.reconstitute(id, name, null);
  }

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
    when(companies.findByName("ACME")).thenReturn(Optional.of(company(ID, "ACME")));

    assertThatExceptionOfType(CompanyNameAlreadyUsedException.class)
      .isThrownBy(() -> service.create("ACME", null))
      .satisfies(exception -> assertThat(exception.existingId()).isEqualTo(ID));
  }

  @Test
  void given_existingCompany_when_updating_then_savesTheCompany()
  {
    when(companies.findById(ID)).thenReturn(Optional.of(company(ID, "Old name")));
    when(companies.findByName("New name")).thenReturn(Optional.empty());
    when(companies.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.update(ID, "New name", null);

    verify(companies).save(any(Company.class));
  }

  @Test
  void given_missingCompany_when_updating_then_throwsNotFound()
  {
    when(companies.findById(ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(ID, "New name", null))
      .isInstanceOf(CompanyNotFoundException.class);
  }

  @Test
  void given_nameTakenByAnotherCompany_when_updating_then_throwsNameAlreadyUsed()
  {
    when(companies.findById(ID)).thenReturn(Optional.of(company(ID, "Old name")));
    when(companies.findByName("Taken")).thenReturn(Optional.of(company(OTHER_ID, "Taken")));

    assertThatThrownBy(() -> service.update(ID, "Taken", null))
      .isInstanceOf(CompanyNameAlreadyUsedException.class);
  }

  @Test
  void given_unusedCompany_when_deleting_then_deletes()
  {
    when(companies.findById(ID)).thenReturn(Optional.of(company(ID, "ACME")));
    when(applications.existsByCompany(ID)).thenReturn(false);

    service.delete(ID);

    verify(companies).delete(ID);
  }

  @Test
  void given_missingCompany_when_deleting_then_throwsNotFound()
  {
    when(companies.findById(ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.delete(ID))
      .isInstanceOf(CompanyNotFoundException.class);
  }

  @Test
  void given_companyStillUsed_when_deleting_then_throwsInUse()
  {
    when(companies.findById(ID)).thenReturn(Optional.of(company(ID, "ACME")));
    when(applications.existsByCompany(ID)).thenReturn(true);

    assertThatThrownBy(() -> service.delete(ID))
      .isInstanceOf(CompanyInUseException.class);
  }

  @Test
  void given_companyStillUsed_when_deleting_then_neverDeletes()
  {
    when(companies.findById(ID)).thenReturn(Optional.of(company(ID, "ACME")));
    when(applications.existsByCompany(ID)).thenReturn(true);
    catchThrowable(() -> service.delete(ID));

    verify(companies, never()).delete(any());
  }

  @Test
  void given_existingId_when_gettingById_then_returnsTheCompany()
  {
    when(companies.findById(ID)).thenReturn(Optional.of(company(ID, "ACME")));

    assertThat(service.byId(ID).name()).isEqualTo("ACME");
  }

  @Test
  void given_storedCompanies_when_listingAll_then_returnsThem()
  {
    when(companies.findAll()).thenReturn(List.of(Company.create("ACME", null)));

    assertThat(service.all()).hasSize(1);
  }
}
