package com.petitcaillou.infra.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtPropertiesTest
{
  private final JwtProperties properties = new JwtProperties();

  @Test
  void given_secret_when_set_then_getterReturnsIt()
  {
    properties.setSecret("a-secret");

    assertThat(properties.getSecret()).isEqualTo("a-secret");
  }

  @Test
  void given_ttl_when_set_then_getterReturnsIt()
  {
    properties.setTtlSeconds(120);

    assertThat(properties.getTtlSeconds()).isEqualTo(120);
  }

  @Test
  void given_issuer_when_set_then_getterReturnsIt()
  {
    properties.setIssuer("issuer");

    assertThat(properties.getIssuer()).isEqualTo("issuer");
  }
}
