package com.petitcaillou.domain.company;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NormalizedNameTest
{
  @Test
  void given_caseAndAccents_when_normalizing_then_collapsesToCanonicalForm()
  {
    assertThat(NormalizedName.of("Éléphant Bleu").value()).isEqualTo("elephant bleu");
  }

  @Test
  void given_punctuationAndDashes_when_normalizing_then_becomeSpaces()
  {
    assertThat(NormalizedName.of("ACME-Corp.").value()).isEqualTo("acme");
  }

  @Test
  void given_legalSuffix_when_normalizing_then_suffixIsDropped()
  {
    assertThat(NormalizedName.of("Datadog SAS").value()).isEqualTo("datadog");
  }

  @Test
  void given_variantsOfSameCompany_when_normalizing_then_theyMatch()
  {
    assertThat(NormalizedName.of("Acme")).isEqualTo(NormalizedName.of("  acme  "));
  }

  @Test
  void given_nameThatNormalizesToNothing_when_normalizing_then_throws()
  {
    assertThatThrownBy(() -> NormalizedName.of("!!!"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_nullText_when_normalizing_then_returnsEmpty()
  {
    assertThat(NormalizedName.text(null)).isEmpty();
  }
}
