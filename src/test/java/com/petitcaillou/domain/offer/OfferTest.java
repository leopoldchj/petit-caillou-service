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
    assertThat(Offer.create(SystemAccount.USERNAME, DETAILS, true).isPublic()).isTrue();
  }

  @Test
  void given_userCreator_when_created_then_isNotPublic()
  {
    assertThat(Offer.create(USER, DETAILS, true).isPublic()).isFalse();
  }

  @Test
  void given_publicOffer_when_checkingVisibility_then_visibleToAnyUser()
  {
    assertThat(Offer.create(SystemAccount.USERNAME, DETAILS, true).isVisibleTo(USER)).isTrue();
  }

  @Test
  void given_privateOffer_when_checkingVisibilityForAnother_then_notVisible()
  {
    assertThat(Offer.create(USER, DETAILS, true).isVisibleTo(Username.of("bob"))).isFalse();
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
  void given_externalId_when_ingested_then_dedupKeyIsTheSourceIdentity()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", "42");

    assertThat(offer.dedupKey()).isEqualTo(ContentHash.ofSource("board", "42"));
  }

  @Test
  void given_noExternalId_when_ingested_then_dedupKeyFallsBackToContent()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", null);

    assertThat(offer.dedupKey())
      .isEqualTo(ContentHash.of(COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30)));
  }

  @Test
  void given_ingestedOffer_when_enriched_then_dedupKeyIsUnchanged()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", "42");

    OfferDetails changed = new OfferDetails(COMPANY, "Senior Backend Engineer", "Lyon",
      LocalDate.of(2026, 8, 30), "https://x/2", "Updated");

    assertThat(offer.enrichedWith(changed).dedupKey()).isEqualTo(offer.dedupKey());
  }

  @Test
  void given_sameContent_when_comparing_then_reportsEqual()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", "42");

    assertThat(offer.hasSameContent(offer.enrichedWith(DETAILS))).isTrue();
  }

  @Test
  void given_changedContent_when_comparing_then_reportsDifferent()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", "42");
    OfferDetails changed = new OfferDetails(COMPANY, "Senior Backend Engineer", "Paris",
      LocalDate.of(2026, 8, 30), "https://x/1", "A role");

    assertThat(offer.hasSameContent(offer.enrichedWith(changed))).isFalse();
  }
}
