package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;

import static org.assertj.core.api.Assertions.assertThat;

class OfferMapperTest
{
  private static final CompanyId COMPANY = CompanyId.of(UUID.randomUUID());
  private static final OfferDetails DETAILS = new OfferDetails(
    COMPANY, "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "role");

  @Test
  void given_offer_when_mappingToEntity_then_storesTheDedupKey()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", "42");

    OfferEntity entity = OfferMapper.toEntity(offer);

    assertThat(entity.getContentHash()).isEqualTo(offer.dedupKey().value());
  }

  @Test
  void given_entity_when_mappingToDomain_then_preservesTheDedupKey()
  {
    Offer offer = Offer.ingested(DETAILS, false, "board", "42");

    Offer roundTripped = OfferMapper.toDomain(OfferMapper.toEntity(offer));

    assertThat(roundTripped.dedupKey()).isEqualTo(offer.dedupKey());
  }

  @Test
  void given_entity_when_mappingToDomain_then_copiesTheTitle()
  {
    OfferEntity entity = new OfferEntity(UUID.randomUUID().toString(), COMPANY.value().toString(),
      "Backend Engineer", "Paris", LocalDate.of(2026, 8, 30), "https://x/1", "role", "system", true, "key");

    assertThat(OfferMapper.toDomain(entity).title()).isEqualTo("Backend Engineer");
  }
}
