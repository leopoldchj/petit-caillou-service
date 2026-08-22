package com.petitcaillou.infra.persistence;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperTest
{
  private static final UUID ID = UUID.randomUUID();

  @Test
  void given_domainCompany_when_mappingToEntity_then_copiesTheId()
  {
    Company company = Company.reconstitute(CompanyId.of(ID), "ACME", "https://acme.example.com");

    CompanyEntity entity = CompanyMapper.toEntity(company);

    assertThat(entity.getId()).isEqualTo(ID.toString());
  }

  @Test
  void given_entity_when_mappingToDomain_then_copiesTheName()
  {
    CompanyEntity entity = new CompanyEntity(ID.toString(), "ACME", "https://acme.example.com");

    Company company = CompanyMapper.toDomain(entity);

    assertThat(company.name()).isEqualTo("ACME");
  }
}
