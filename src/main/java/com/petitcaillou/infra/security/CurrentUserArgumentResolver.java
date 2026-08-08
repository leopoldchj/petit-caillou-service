package com.petitcaillou.infra.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.petitcaillou.application.CurrentUser;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver
{
  @Override
  public boolean supportsParameter(MethodParameter parameter)
  {
    return parameter.hasParameterAnnotation(CurrentUser.class)
      && parameter.getParameterType().equals(AuthenticatedUser.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
    NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
  {
    Jwt jwt = currentJwt();
    return new AuthenticatedUser(
      Username.of(jwt.getSubject()),
      Email.of(jwt.getClaimAsString("email")),
      Role.valueOf(jwt.getClaimAsString("role")));
  }

  private Jwt currentJwt()
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt)
    {
      return jwt;
    }

    throw new IllegalStateException("No authenticated JWT in the security context");
  }
}
