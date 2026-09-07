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
  void given_punctuatedLongSuffix_when_normalizing_then_suffixIsDropped()
  {
    assertThat(NormalizedName.of("Société Exemple S.A.S.").value()).isEqualTo("societe exemple");
  }

  @Test
  void given_punctuatedLlc_when_normalizing_then_suffixIsDropped()
  {
    assertThat(NormalizedName.of("Acme L.L.C.").value()).isEqualTo("acme");
  }

  @Test
  void given_ligaturesAndSuffix_when_normalizing_then_expandsAndStrips()
  {
    assertThat(NormalizedName.of("Cœur & Associés SARL").value()).isEqualTo("coeur associes");
  }

  @Test
  void given_fullWidthLetters_when_normalizing_then_foldsToAscii()
  {
    assertThat(NormalizedName.of("ＡＣＭＥ Ltd").value()).isEqualTo("acme");
  }

  @Test
  void given_trailingInitialsMatchingShortSuffix_when_normalizing_then_kept()
  {
    assertThat(NormalizedName.of("Studio B V").value()).isEqualTo("studio b v");
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
}
