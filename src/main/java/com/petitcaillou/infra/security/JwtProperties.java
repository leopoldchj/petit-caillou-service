package com.petitcaillou.infra.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petit-caillou.jwt")
public class JwtProperties
{
  private String secret = "";

  private long ttlSeconds = 3600;

  private String issuer = "petit-caillou-service";

  public String getSecret()
  {
    return secret;
  }

  public void setSecret(String secret)
  {
    this.secret = secret;
  }

  public long getTtlSeconds()
  {
    return ttlSeconds;
  }

  public void setTtlSeconds(long ttlSeconds)
  {
    this.ttlSeconds = ttlSeconds;
  }

  public String getIssuer()
  {
    return issuer;
  }

  public void setIssuer(String issuer)
  {
    this.issuer = issuer;
  }
}
