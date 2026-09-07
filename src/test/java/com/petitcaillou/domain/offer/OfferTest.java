package com.petitcaillou.domain.offer;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferTest
{
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final Username USER = Username.of("alice");
  private static final OfferDetails DETAILS = new OfferDetails(
    COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "A role");

  @Test
  void given_systemCreator_when_created_then_isPublic()
  {
    Offer offer = Offer.create(SystemAccount.USERNAME, DETAILS, true);

    assertThat(offer.isPublic()).isTrue();
  }

  @Test
  void given_userCreator_when_created_then_isNotPublic()
  {
    Offer offer = Offer.create(USER, DETAILS, true);

    assertThat(offer.isPublic()).isFalse();
  }

  @Test
  void given_publicOffer_when_checkingVisibility_then_visibleToAnyUser()
  {
    Offer offer = Offer.create(SystemAccount.USERNAME, DETAILS, true);

    assertThat(offer.isVisibleTo(USER)).isTrue();
  }

  @Test
  void given_privateOffer_when_checkingVisibilityForAnother_then_notVisible()
  {
    Offer offer = Offer.create(USER, DETAILS, true);

    assertThat(offer.isVisibleTo(Username.of("bob"))).isFalse();
  }

  @Test
  void given_missingTitle_when_created_then_throws()
  {
    OfferDetails invalid = new OfferDetails(COMPANY, " ", "Paris", LocalDate.of(2026, 8, 30), null, null);

    assertThatThrownBy(() -> Offer.create(USER, invalid, true))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_missingPublicationDate_when_created_then_throws()
  {
    OfferDetails invalid = new OfferDetails(COMPANY, "Backend", "Paris", null, null, null);

    assertThatThrownBy(() -> Offer.create(USER, invalid, true))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_details_when_created_then_computesAContentHash()
  {
    Offer offer = Offer.create(USER, DETAILS, true);

    assertThat(offer.contentHash()).isEqualTo(ContentHash.of(COMPANY, "Backend Engineer", "Paris",
      LocalDate.of(2026, 8, 30)));
  }
}
