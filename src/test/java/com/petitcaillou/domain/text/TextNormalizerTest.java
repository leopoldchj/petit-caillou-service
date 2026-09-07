package com.petitcaillou.domain.text;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizerTest
{
  @Test
  void given_caseAndAccents_when_folding_then_stripsThem()
  {
    assertThat(TextNormalizer.fold("Paris, Île-de-France")).isEqualTo("paris ile de france");
  }

  @Test
  void given_ligatures_when_folding_then_expandsThem()
  {
    assertThat(TextNormalizer.fold("Cœur")).isEqualTo("coeur");
  }

  @Test
  void given_fullWidthLetters_when_folding_then_foldsToAscii()
  {
    assertThat(TextNormalizer.fold("ＡＣＭＥ")).isEqualTo("acme");
  }

  @Test
  void given_null_when_folding_then_returnsEmpty()
  {
    assertThat(TextNormalizer.fold(null)).isEmpty();
  }

  @Test
  void given_onlySeparators_when_folding_then_returnsEmpty()
  {
    assertThat(TextNormalizer.fold("  --  ")).isEmpty();
  }
}
