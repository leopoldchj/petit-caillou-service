package com.petitcaillou.domain.offer;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;

import static org.assertj.core.api.Assertions.assertThat;

class ContentHashTest
{
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final LocalDate DATE = LocalDate.of(2026, 8, 30);

  @Test
  void given_sameInputs_when_hashing_then_isDeterministic()
  {
    assertThat(ContentHash.of(COMPANY, "Backend Engineer", "Paris", DATE))
      .isEqualTo(ContentHash.of(COMPANY, "Backend Engineer", "Paris", DATE));
  }

  @Test
  void given_titleDifferingByCaseAndAccents_when_hashing_then_isTheSame()
  {
    assertThat(ContentHash.of(COMPANY, "Ingénieur", "Paris", DATE))
      .isEqualTo(ContentHash.of(COMPANY, "INGENIEUR", "paris", DATE));
  }

  @Test
  void given_differentPublicationDate_when_hashing_then_differs()
  {
    assertThat(ContentHash.of(COMPANY, "Backend Engineer", "Paris", DATE))
      .isNotEqualTo(ContentHash.of(COMPANY, "Backend Engineer", "Paris", DATE.plusDays(1)));
  }

  @Test
  void given_rawValue_when_wrapping_then_keepsIt()
  {
    assertThat(ContentHash.ofValue("abc").value()).isEqualTo("abc");
  }
}
