package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.user.Username;

import static org.assertj.core.api.Assertions.assertThat;

class OfferMapperTest
{
  private static final UUID ID = UUID.randomUUID();
  private static final UUID COMPANY = UUID.randomUUID();
  private static final OfferDetails DETAILS = new OfferDetails(
    CompanyId.of(COMPANY), "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "role");

  @Test
  void given_domainOffer_when_mappingToEntity_then_copiesTheContentHash()
  {
    Offer offer = Offer.reconstitute(
      com.petitcaillou.domain.offer.OfferId.of(ID), Username.of("alice"), DETAILS, true);

    OfferEntity entity = OfferMapper.toEntity(offer);

    assertThat(entity.getContentHash()).isEqualTo(offer.contentHash().value());
  }

  @Test
  void given_entity_when_mappingToDomain_then_copiesTheTitle()
  {
    OfferEntity entity = new OfferEntity(
      ID.toString(), COMPANY.toString(), "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30),
      "https://x/1", "role", "alice", true, "hash");

    Offer offer = OfferMapper.toDomain(entity);

    assertThat(offer.title()).isEqualTo("Backend Engineer");
  }

  @Test
  void given_systemEntity_when_mappingToDomain_then_offerIsPublic()
  {
    OfferEntity entity = new OfferEntity(
      ID.toString(), COMPANY.toString(), "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30),
      "https://x/1", "role", "system", true, "hash");

    Offer offer = OfferMapper.toDomain(entity);

    assertThat(offer.isPublic()).isTrue();
  }
}
