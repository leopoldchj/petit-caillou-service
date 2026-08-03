package com.petitcaillou.infra.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.petitcaillou.application.CurrentUser;
import com.petitcaillou.domain.authentication.AuthenticatedUser;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserArgumentResolverTest
{
  private final CurrentUserArgumentResolver resolver = new CurrentUserArgumentResolver();

  @AfterEach
  void clearContext()
  {
    SecurityContextHolder.clearContext();
  }

  @SuppressWarnings("unused")
  private void sample(@CurrentUser AuthenticatedUser user)
  {
  }

  @Test
  void given_annotatedAuthenticatedUserParameter_when_checkingSupport_then_returnsTrue() throws Exception
  {
    MethodParameter parameter = new MethodParameter(
      getClass().getDeclaredMethod("sample", AuthenticatedUser.class), 0);

    assertThat(resolver.supportsParameter(parameter)).isTrue();
  }

  @Test
  void given_jwtInSecurityContext_when_resolving_then_mapsSubjectToAlias()
  {
    Jwt jwt = Jwt.withTokenValue("token")
      .header("alg", "HS256")
      .subject("john_doe")
      .claim("email", "user@app.com")
      .claim("role", "USER")
      .build();
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

    AuthenticatedUser current = (AuthenticatedUser) resolver.resolveArgument(null, null, null, null);

    assertThat(current.alias().value()).isEqualTo("john_doe");
  }
}
