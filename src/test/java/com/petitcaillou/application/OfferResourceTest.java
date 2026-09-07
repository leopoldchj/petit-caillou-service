package com.petitcaillou.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.application.dto.OfferRequest;
import com.petitcaillou.application.dto.OfferView;
import com.petitcaillou.application.dto.PageView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.company.Company;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.OfferService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferResourceTest
{
  private static final Username USER = Username.of("alice");
  private static final AuthenticatedUser CURRENT = new AuthenticatedUser(USER, Email.of("alice@app.com"), Role.USER);
  private static final UUID ID = UUID.randomUUID();
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final OfferRequest REQUEST = new OfferRequest(
    COMPANY.value().toString(), "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "role");

  private final OfferService offerService = mock(OfferService.class);
  private final CompanyService companyService = mock(CompanyService.class);
  private final OfferResource resource = new OfferResource(offerService, companyService);

  private Offer offer()
  {
    OfferDetails details = new OfferDetails(
      COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "role");
    return Offer.reconstitute(OfferId.of(ID), USER, details, true);
  }

  private Company company()
  {
    return Company.reconstitute(COMPANY, "ACME", null);
  }

  @Test
  void given_request_when_creating_then_returnsTheCreatedOfferId()
  {
    when(offerService.createPrivate(eq(USER), any())).thenReturn(offer());
    when(companyService.byId(COMPANY)).thenReturn(company());

    OfferView view = resource.create(CURRENT, REQUEST);

    assertThat(view.id()).isEqualTo(ID.toString());
  }

  @Test
  void given_id_when_gettingById_then_embedsTheCompany()
  {
    when(offerService.byId(USER, OfferId.of(ID))).thenReturn(offer());
    when(companyService.byId(COMPANY)).thenReturn(company());

    OfferView view = resource.get(CURRENT, ID.toString());

    assertThat(view.company().name()).isEqualTo("ACME");
  }

  @Test
  void given_catalog_when_listing_then_returnsAPageOfOffers()
  {
    when(offerService.list(eq(USER), any())).thenReturn(new Page<>(List.of(offer()), 0, 20, 1));
    when(companyService.byId(COMPANY)).thenReturn(company());

    PageView<OfferView> page = resource.list(CURRENT, null, null);

    assertThat(page.items()).hasSize(1);
  }
}
