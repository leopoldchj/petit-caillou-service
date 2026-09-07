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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

  private String createOffer(String token, String companyId, String title) throws Exception
  {
    String body = mockMvc.perform(post("/offers")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"companyId\":\"%s\",\"title\":\"%s\",\"publicationDate\":\"2026-01-15\"}".formatted(companyId, title)))
      .andReturn().getResponse().getContentAsString();
    return extractId(body);
  }

  private String apply(String token, String offerId) throws Exception
  {
    String body = mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"offerId\":\"%s\",\"responseStatus\":\"INTERVIEW\"}".formatted(offerId)))
      .andReturn().getResponse().getContentAsString();
    return extractId(body);
  }

  @Test
  void given_authenticatedUser_when_creatingOffer_then_respondsCreated() throws Exception
  {
    String token = registerAndGetToken("offer_creator", "offer_creator@ex.com");
    String companyId = createCompany(token, "Offer Creator Co");

    mockMvc.perform(post("/offers")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"companyId\":\"%s\",\"title\":\"Backend\",\"publicationDate\":\"2026-01-15\"}".formatted(companyId)))
      .andExpect(status().isCreated());
  }

  @Test
  void given_offerMissingPublicationDate_when_creating_then_respondsBadRequest() throws Exception
  {
    String token = registerAndGetToken("offer_no_date", "offer_no_date@ex.com");
    String companyId = createCompany(token, "No Date Co");

    mockMvc.perform(post("/offers")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"companyId\":\"%s\",\"title\":\"Backend\"}".formatted(companyId)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void given_ownOffers_when_listing_then_returnsThemPaginated() throws Exception
  {
    String token = registerAndGetToken("offer_lister", "offer_lister@ex.com");
    String companyId = createCompany(token, "Offer Lister Co");
    createOffer(token, companyId, "Backend");
    createOffer(token, companyId, "Frontend");

    mockMvc.perform(get("/offers").param("size", "1").header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(jsonPath("$.items.length()").value(1))
      .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void given_privateOffer_when_anotherUserGetsIt_then_respondsNotFound() throws Exception
  {
    String ownerToken = registerAndGetToken("offer_owner", "offer_owner@ex.com");
    String companyId = createCompany(ownerToken, "Offer Owner Co");
    String offerId = createOffer(ownerToken, companyId, "Backend");
    String intruderToken = registerAndGetToken("offer_intruder", "offer_intruder@ex.com");

    mockMvc.perform(get("/offers/" + offerId).header(HttpHeaders.AUTHORIZATION, bearer(intruderToken)))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_companyWithOffers_when_deleting_then_respondsConflict() throws Exception
  {
    String token = registerAndGetToken("company_locked", "company_locked@ex.com");
    String companyId = createCompany(token, "Locked Co");
    createOffer(token, companyId, "Backend");

    mockMvc.perform(delete("/companies/" + companyId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(status().isConflict());
  }

  @Test
  void given_visibleOffer_when_applying_then_respondsCreated() throws Exception
  {
    String token = registerAndGetToken("applier", "applier@ex.com");
    String companyId = createCompany(token, "Applier Co");
    String offerId = createOffer(token, companyId, "Backend");

    mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"offerId\":\"%s\"}".formatted(offerId)))
      .andExpect(status().isCreated());
  }

  @Test
  void given_unknownOffer_when_applying_then_respondsNotFound() throws Exception
  {
    String token = registerAndGetToken("no_offer", "no_offer@ex.com");

    mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"offerId\":\"%s\"}".formatted(UUID.randomUUID())))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_alreadyApplied_when_applyingAgain_then_respondsConflict() throws Exception
  {
    String token = registerAndGetToken("dup_applier", "dup_applier@ex.com");
    String companyId = createCompany(token, "Dup Applier Co");
    String offerId = createOffer(token, companyId, "Backend");
    apply(token, offerId);

    mockMvc.perform(post("/applications")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"offerId\":\"%s\"}".formatted(offerId)))
      .andExpect(status().isConflict());
  }

  @Test
  void given_ownApplications_when_listing_then_returnsThemPaginated() throws Exception
  {
    String token = registerAndGetToken("app_lister", "app_lister@ex.com");
    String companyId = createCompany(token, "App Lister Co");
    String offerId = createOffer(token, companyId, "Backend");
    apply(token, offerId);

    mockMvc.perform(get("/applications").header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(jsonPath("$.items.length()").value(1));
  }

  @Test
  void given_ownApplication_when_gettingById_then_returnsItWithOfferCompany() throws Exception
  {
    String token = registerAndGetToken("app_getter", "app_getter@ex.com");
    String companyId = createCompany(token, "App Getter Co");
    String offerId = createOffer(token, companyId, "Backend");
    String id = apply(token, offerId);

    mockMvc.perform(get("/applications/" + id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(jsonPath("$.offer.company.name").value("App Getter Co"));
  }

  @Test
  void given_ownApplication_when_updatingStatus_then_respondsOk() throws Exception
  {
    String token = registerAndGetToken("app_updater", "app_updater@ex.com");
    String companyId = createCompany(token, "App Updater Co");
    String offerId = createOffer(token, companyId, "Backend");
    String id = apply(token, offerId);

    mockMvc.perform(patch("/applications/" + id)
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"responseStatus\":\"ACCEPTED\"}"))
      .andExpect(status().isOk());
  }

  @Test
  void given_ownApplication_when_deleting_then_respondsNoContent() throws Exception
  {
    String token = registerAndGetToken("app_remover", "app_remover@ex.com");
    String companyId = createCompany(token, "App Remover Co");
    String offerId = createOffer(token, companyId, "Backend");
    String id = apply(token, offerId);

    mockMvc.perform(delete("/applications/" + id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
      .andExpect(status().isNoContent());
  }

  @Test
  void given_applicationOfAnotherUser_when_gettingById_then_respondsNotFound() throws Exception
  {
    String ownerToken = registerAndGetToken("app_owner", "app_owner@ex.com");
    String companyId = createCompany(ownerToken, "App Owner Co");
    String offerId = createOffer(ownerToken, companyId, "Backend");
    String id = apply(ownerToken, offerId);
    String intruderToken = registerAndGetToken("app_intruder", "app_intruder@ex.com");

    mockMvc.perform(get("/applications/" + id).header(HttpHeaders.AUTHORIZATION, bearer(intruderToken)))
      .andExpect(status().isNotFound());
  }

  @Test
  void given_noToken_when_listingApplications_then_respondsUnauthorized() throws Exception
  {
    mockMvc.perform(get("/applications"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void given_duplicateCompanyName_when_creating_then_respondsConflictWithExistingId() throws Exception
  {
    String token = registerAndGetToken("dup_company", "dup_company@ex.com");
    createCompany(token, "Duplicate Co");

    mockMvc.perform(post("/companies")
        .header(HttpHeaders.AUTHORIZATION, bearer(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"duplicate co\"}"))
      .andExpect(jsonPath("$.existingCompanyId").isNotEmpty());
  }
}
