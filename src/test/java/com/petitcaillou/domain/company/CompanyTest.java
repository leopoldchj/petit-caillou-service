package com.petitcaillou.domain.company;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompanyTest
{
  @Test
  void given_nullName_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> Company.create(null, null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_blankName_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> Company.create("   ", null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_nameAndWebsite_when_creating_then_exposesTheName()
  {
    Company company = Company.create("ACME", "https://acme.example.com");

    assertThat(company.name()).isEqualTo("ACME");
  }
}
