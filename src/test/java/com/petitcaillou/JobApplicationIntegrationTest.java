package com.petitcaillou;

import java.util.UUID;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
class JobApplicationIntegrationTest
{
  private static final Pattern ACCESS_TOKEN = Pattern.compile("\"accessToken\"\\s*:\\s*\"([^\"]+)\"");
  private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

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

  private String bearer(String token)
  {
    return "Bearer " + token;
  }

  private String extractId(String body)
  {
    Matcher matcher = ID.matcher(body);
    matcher.find();
    return matcher.group(1);
  }

  private String registerAndGetToken(String username, String email) throws Exception
  {
    String body = mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"Password1\"}".formatted(username, email)))
      .andReturn().getResponse().getContentAsString();
    Matcher matcher = ACCESS_TOKEN.matcher(body);
    matcher.find();
    return matcher.group(1);
  }

  private String createCompany(String token, String name) throws Exception
  {
    String body = mockMvc.perform(post("/companies")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"%s\"}".formatted(name)))
      .andReturn().getResponse().getContentAsString();
    return extractId(body);
  }

  private String applicationJson(String companyId)
  {
    return "{\"companyId\":\"%s\",\"title\":\"Backend engineer\",\"responseStatus\":\"INTERVIEW\"}".formatted(companyId);
  }

  private String createApplication(String token, String companyId) throws Exception
  {
    String body = mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(applicationJson(companyId)))
      .andReturn().getResponse().getContentAsString();
    return extractId(body);
  }

  @Test
  void given_authenticatedUser_when_creatingCompany_then_respondsCreated() throws Exception
  {
    String token = registerAndGetToken("company_creator", "company_creator@ex.com");

    mockMvc.perform(post("/companies")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Company Creator Co\"}"))
      .andExpect(status().isCreated());
  }

  @Test
  void given_authenticatedUser_when_listingCompanies_then_respondsOk() throws Exception
  {
    String token = registerAndGetToken("company_lister", "company_lister@ex.com");

    mockMvc.perform(get("/companies").header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(status().isOk());
  }

  @Test
  void given_existingName_when_creatingCompany_then_respondsConflictWithExistingId() throws Exception
  {
    String token = registerAndGetToken("dup_company", "dup_company@ex.com");
    createCompany(token, "Duplicate Co");

    mockMvc.perform(post("/companies")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Duplicate Co\"}"))
      .andExpect(jsonPath("$.existingCompanyId").isNotEmpty());
  }

  @Test
  void given_authenticatedUser_when_updatingCompany_then_respondsOk() throws Exception
  {
    String token = registerAndGetToken("company_editor", "company_editor@ex.com");
    String companyId = createCompany(token, "Editor Co");

    mockMvc.perform(put("/companies/" + companyId)
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Renamed Co\"}"))
      .andExpect(status().isOk());
  }

  @Test
  void given_unusedCompany_when_deleting_then_respondsNoContent() throws Exception
  {
    String token = registerAndGetToken("company_remover", "company_remover@ex.com");
    String companyId = createCompany(token, "Remover Co Global");

    mockMvc.perform(delete("/companies/" + companyId)
        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(status().isNoContent());
  }

  @Test
  void given_companyInUse_when_deleting_then_respondsConflict() throws Exception
  {
    String token = registerAndGetToken("company_locked", "company_locked@ex.com");
    String companyId = createCompany(token, "Locked Co");
    createApplication(token, companyId);

    mockMvc.perform(delete("/companies/" + companyId)
        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(status().isConflict());
  }

  @Test
  void given_knownCompany_when_creatingApplication_then_respondsCreated() throws Exception
  {
    String token = registerAndGetToken("creator", "creator@ex.com");
    String companyId = createCompany(token, "Creator Co");

    mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(applicationJson(companyId)))
      .andExpect(status().isCreated());
  }

  @Test
  void given_unknownCompany_when_creatingApplication_then_respondsNotFound() throws Exception
  {
    String token = registerAndGetToken("no_company", "no_company@ex.com");

    mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(applicationJson(UUID.randomUUID().toString())))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_missingTitle_when_creatingApplication_then_respondsBadRequest() throws Exception
  {
    String token = registerAndGetToken("no_title", "no_title@ex.com");
    String companyId = createCompany(token, "No Title Co");

    mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"companyId\":\"%s\"}".formatted(companyId)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void given_malformedId_when_updatingApplication_then_respondsBadRequest() throws Exception
  {
    String token = registerAndGetToken("bad_id", "bad_id@ex.com");
    String companyId = createCompany(token, "Bad Id Co");

    mockMvc.perform(put("/applications/not-a-uuid")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(applicationJson(companyId)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void given_noToken_when_listingApplications_then_respondsUnauthorized() throws Exception
  {
    mockMvc.perform(get("/applications"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void given_ownApplications_when_listing_then_returnsThem() throws Exception
  {
    String token = registerAndGetToken("lister", "lister@ex.com");
    String companyId = createCompany(token, "Lister Co");
    createApplication(token, companyId);

    mockMvc.perform(get("/applications").header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void given_companyFilter_when_listing_then_returnsOnlyMatchingApplications() throws Exception
  {
    String token = registerAndGetToken("filterer", "filterer@ex.com");
    String companyA = createCompany(token, "Filter A Co");
    String companyB = createCompany(token, "Filter B Co");
    createApplication(token, companyA);
    createApplication(token, companyB);

    mockMvc.perform(get("/applications")
        .param("companyId", companyA)
        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void given_ownApplication_when_gettingById_then_returnsItWithCompanyName() throws Exception
  {
    String token = registerAndGetToken("getter", "getter@ex.com");
    String companyId = createCompany(token, "Getter Co");
    String id = createApplication(token, companyId);

    mockMvc.perform(get("/applications/" + id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(jsonPath("$.company.name").value("Getter Co"));
  }

  @Test
  void given_applicationOfAnotherUser_when_gettingById_then_respondsNotFound() throws Exception
  {
    String ownerToken = registerAndGetToken("owner_get", "owner_get@ex.com");
    String companyId = createCompany(ownerToken, "Owner Get Co");
    String id = createApplication(ownerToken, companyId);
    String intruderToken = registerAndGetToken("intruder_get", "intruder_get@ex.com");

    mockMvc.perform(get("/applications/" + id).header(HttpHeaders.AUTHORIZATION, bearer(intruderToken)))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_ownApplication_when_updating_then_respondsOk() throws Exception
  {
    String token = registerAndGetToken("updater", "updater@ex.com");
    String companyId = createCompany(token, "Updater Co");
    String id = createApplication(token, companyId);

    mockMvc.perform(put("/applications/" + id)
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"companyId\":\"%s\",\"title\":\"Lead engineer\",\"responseStatus\":\"ACCEPTED\"}".formatted(companyId)))
      .andExpect(status().isOk());
  }

  @Test
  void given_unknownCompany_when_updating_then_respondsNotFound() throws Exception
  {
    String token = registerAndGetToken("update_no_company", "update_no_company@ex.com");
    String companyId = createCompany(token, "Update No Company Co");
    String id = createApplication(token, companyId);

    mockMvc.perform(put("/applications/" + id)
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(applicationJson(UUID.randomUUID().toString())))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_ownApplication_when_deleting_then_respondsNoContent() throws Exception
  {
    String token = registerAndGetToken("remover", "remover@ex.com");
    String companyId = createCompany(token, "Remover Co");
    String id = createApplication(token, companyId);

    mockMvc.perform(delete("/applications/" + id)
        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(status().isNoContent());
  }

  @Test
  void given_applicationOfAnotherUser_when_updating_then_respondsNotFound() throws Exception
  {
    String ownerToken = registerAndGetToken("owner_a", "owner_a@ex.com");
    String companyId = createCompany(ownerToken, "Owner A Co");
    String id = createApplication(ownerToken, companyId);
    String intruderToken = registerAndGetToken("intruder_a", "intruder_a@ex.com");

    mockMvc.perform(put("/applications/" + id)
        .header(HttpHeaders.AUTHORIZATION, bearer(intruderToken))
        .contentType(MediaType.APPLICATION_JSON)
        .content(applicationJson(companyId)))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_applicationOfAnotherUser_when_deleting_then_respondsNotFound() throws Exception
  {
    String ownerToken = registerAndGetToken("owner_b", "owner_b@ex.com");
    String companyId = createCompany(ownerToken, "Owner B Co");
    String id = createApplication(ownerToken, companyId);
    String intruderToken = registerAndGetToken("intruder_b", "intruder_b@ex.com");

    mockMvc.perform(delete("/applications/" + id)
        .header(HttpHeaders.AUTHORIZATION, bearer(intruderToken)))
      .andExpect(status().isNotFound());
  }
}
