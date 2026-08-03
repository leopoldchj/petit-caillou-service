package com.petitcaillou.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.petitcaillou.application.dto.AuthResponse;
import com.petitcaillou.application.dto.LoginRequest;
import com.petitcaillou.application.dto.RegisterRequest;
import com.petitcaillou.domain.authentication.AccessToken;
import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.RawPassword;
import com.petitcaillou.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthResource
{
  private final AuthService authService;

  public AuthResource(AuthService authService)
  {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request)
  {
    AccessToken token = authService.register(
      Alias.of(request.alias()),
      Email.of(request.email()),
      RawPassword.of(request.password()));
    return AuthResponse.bearer(token.value());
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request)
  {
    AccessToken token = authService.login(Alias.of(request.alias()), RawPassword.of(request.password()));
    return AuthResponse.bearer(token.value());
  }
}
