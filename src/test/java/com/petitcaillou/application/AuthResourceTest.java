package com.petitcaillou.application;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.petitcaillou.domain.authentication.AccessToken;
import com.petitcaillou.domain.authentication.exceptions.InvalidCredentialsException;
import com.petitcaillou.domain.user.exceptions.UsernameAlreadyUsedException;
import com.petitcaillou.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthResourceTest
{
  private static final String REGISTER_JSON =
    "{\"username\":\"john_doe\",\"email\":\"user@app.com\",\"password\":\"password1\"}";
  private static final String LOGIN_JSON =
    "{\"username\":\"john_doe\",\"password\":\"password1\"}";

  private final AuthService authService = mock(AuthService.class);
  private final MockMvc mockMvc = MockMvcBuilders
    .standaloneSetup(new AuthResource(authService))
    .setControllerAdvice(new ApiExceptionHandler())
    .build();

  @Test
  void given_newAccount_when_postingRegister_then_returnsCreated() throws Exception
  {
    when(authService.register(any(), any(), any())).thenReturn(AccessToken.of("signed"));

    mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(REGISTER_JSON))
      .andExpect(status().isCreated());
  }

  @Test
  void given_takenUsername_when_postingRegister_then_returnsConflict() throws Exception
  {
    when(authService.register(any(), any(), any())).thenThrow(new UsernameAlreadyUsedException());

    mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(REGISTER_JSON))
      .andExpect(status().isConflict());
  }

  @Test
  void given_validCredentials_when_postingLogin_then_returnsOk() throws Exception
  {
    when(authService.login(any(), any())).thenReturn(AccessToken.of("signed"));

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN_JSON))
      .andExpect(status().isOk());
  }

  @Test
  void given_wrongCredentials_when_postingLogin_then_returnsUnauthorized() throws Exception
  {
    when(authService.login(any(), any())).thenThrow(new InvalidCredentialsException());

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN_JSON))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void given_wrongCredentials_when_postingLogin_then_bodyIsProblemJson() throws Exception
  {
    when(authService.login(any(), any())).thenThrow(new InvalidCredentialsException());

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN_JSON))
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
  }
}
