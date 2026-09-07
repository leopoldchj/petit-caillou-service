package com.petitcaillou.domain.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UrlTest
{
  @ParameterizedTest
  @ValueSource(strings = {
    "https://jobs.example/#/offers/42",
    "https://jobs.example/?q=a%20b#/offers/a%2Fb",
    "https://jobs.example/#"
  })
  void given_fragment_when_created_then_preservesTheLink(String link)
  {
    assertThat(Url.of(link).value()).isEqualTo(link);
  }

  @Test
  void given_httpsUrl_when_created_then_isSecure()
  {
    assertThat(Url.of("https://acme.example/jobs").isSecure()).isTrue();
  }

  @Test
  void given_url_when_created_then_exposesTheHost()
  {
    assertThat(Url.of("https://Jobs.Acme.EXAMPLE/list").host()).isEqualTo("jobs.acme.example");
  }

  @Test
  void given_defaultPortAndCasing_when_created_then_canonicalizes()
  {
    assertThat(Url.of("HTTPS://Acme.Example:443/Jobs").value()).isEqualTo("https://acme.example/Jobs");
  }

  @Test
  void given_sameAddressDifferentCasing_when_compared_then_areEqual()
  {
    assertThat(Url.of("https://ACME.example/")).isEqualTo(Url.of("https://acme.example/"));
  }

  @Test
  void given_nonHttpScheme_when_created_then_throws()
  {
    assertThatThrownBy(() -> Url.of("ftp://acme.example"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_garbage_when_created_then_throws()
  {
    assertThatThrownBy(() -> Url.of("not a url"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_blank_when_creatingNullable_then_returnsNull()
  {
    assertThat(Url.ofNullable("  ")).isNull();
  }
}
