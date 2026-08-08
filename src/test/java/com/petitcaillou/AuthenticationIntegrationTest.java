package com.petitcaillou;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
class AuthenticationIntegrationTest
{
  private static final Pattern ACCESS_TOKEN = Pattern.compile("\"accessToken\"\\s*:\\s*\"([^\"]+)\"");

  @Container
  @ServiceConnection
  static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.11");

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp()
  {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  private String registerJson(String username, String email, String password)
  {
    return "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}".formatted(username, email, password);
  }

  private String loginJson(String username, String password)
  {
    return "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password);
  }

  private String registerAndGetToken(String username, String email) throws Exception
  {
    String body = mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerJson(username, email, "Password1")))
      .andReturn().getResponse().getContentAsString();
    Matcher matcher = ACCESS_TOKEN.matcher(body);
    matcher.find();
    return matcher.group(1);
  }

  @Test
  void given_newCredentials_when_registering_then_respondsCreated() throws Exception
  {
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerJson("newbie", "register@ex.com", "Password1")))
      .andExpect(status().isCreated());
  }

  @Test
  void given_usernameAlreadyRegistered_when_registeringAgain_then_respondsConflict() throws Exception
  {
    registerAndGetToken("dupusername", "dup1@ex.com");

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerJson("dupusername", "dup2@ex.com", "Password1")))
      .andExpect(status().isConflict());
  }

  @Test
  void given_registeredUser_when_loggingInWithGoodPassword_then_returnsToken() throws Exception
  {
    registerAndGetToken("loginuser", "login@ex.com");

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginJson("loginuser", "Password1")))
      .andExpect(jsonPath("$.accessToken").isNotEmpty());
  }

  @Test
  void given_registeredUser_when_loggingInWithWrongPassword_then_respondsUnauthorized() throws Exception
  {
    registerAndGetToken("wrongpwuser", "wrongpw@ex.com");

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginJson("wrongpwuser", "wrong-password")))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void given_noToken_when_callingProtectedEndpoint_then_respondsUnauthorized() throws Exception
  {
    mockMvc.perform(get("/me"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void given_tokenFromRegistration_when_callingProtectedEndpoint_then_returnsCurrentUserUsername() throws Exception
  {
    String token = registerAndGetToken("meuser", "me@ex.com");

    mockMvc.perform(get("/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
      .andExpect(jsonPath("$.username").value("meuser"));
  }
}
