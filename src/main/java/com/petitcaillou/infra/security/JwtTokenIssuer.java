package com.petitcaillou.infra.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.petitcaillou.domain.authentication.AccessToken;
import com.petitcaillou.domain.authentication.TokenIssuer;
import com.petitcaillou.domain.user.User;

@Component
public class JwtTokenIssuer implements TokenIssuer
{
  private final JwtEncoder encoder;
  private final JwtProperties properties;

  public JwtTokenIssuer(JwtEncoder encoder, JwtProperties properties)
  {
    this.encoder = encoder;
    this.properties = properties;
  }

  @Override
  public AccessToken issueFor(User user)
  {
    Instant now = Instant.now();

    JwtClaimsSet claims = JwtClaimsSet.builder()
      .issuer(properties.getIssuer())
      .issuedAt(now)
      .expiresAt(now.plus(properties.getTtlSeconds(), ChronoUnit.SECONDS))
      .subject(user.alias().value())
      .claim("email", user.email().value())
      .claim("role", user.role().name())
      .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

    return AccessToken.of(token);
  }
}
