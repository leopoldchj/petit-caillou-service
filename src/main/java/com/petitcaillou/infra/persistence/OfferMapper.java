package com.petitcaillou.infra.persistence;

import java.util.UUID;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.user.Username;

final class OfferMapper
{
  private OfferMapper()
  {
  }

  static OfferEntity toEntity(Offer offer)
  {
    return new OfferEntity(
      offer.id().value().toString(),
      offer.companyId().value().toString(),
      offer.title(),
      offer.location(),
      offer.publicationDate(),
      offer.link(),
      offer.description(),
      offer.createdBy().value(),
      offer.verified(),
      offer.contentHash().value());
  }

  static Offer toDomain(OfferEntity entity)
  {
    OfferDetails details = new OfferDetails(
      CompanyId.of(UUID.fromString(entity.getCompanyId())),
      entity.getTitle(),
      entity.getLocation(),
      entity.getPublicationDate(),
      entity.getLink(),
      entity.getDescription());

    return Offer.reconstitute(
      OfferId.of(UUID.fromString(entity.getId())),
      Username.of(entity.getCreatedBy()),
      details,
      entity.isVerified());
  }
}
