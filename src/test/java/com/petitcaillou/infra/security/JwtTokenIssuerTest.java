package com.petitcaillou.infra.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.petitcaillou.domain.authentication.AccessToken;
import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.HashedPassword;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenIssuerTest
{
  private final JwtEncoder encoder = mock(JwtEncoder.class);
  private final JwtProperties properties = new JwtProperties();
  private final JwtTokenIssuer issuer = new JwtTokenIssuer(encoder, properties);

  @Test
  void given_user_when_issuingToken_then_returnsEncodedTokenValue()
  {
    Jwt jwt = Jwt.withTokenValue("signed-jwt")
      .header("alg", "HS256")
      .subject("john_doe")
      .build();
    when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
    User user = User.reconstitute(Alias.of("john_doe"), Email.of("a@b.com"), HashedPassword.of("h"), Role.USER);

    AccessToken token = issuer.issueFor(user);

    assertThat(token.value()).isEqualTo("signed-jwt");
  }
}
