package com.petitcaillou.domain.offer;

import java.util.Optional;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

public interface OfferRepository
{
  Offer save(Offer offer);

  Optional<Offer> findById(OfferId id);

  boolean existsByCompany(CompanyId companyId);

  Optional<Offer> findByHashAndCreatedBy(ContentHash hash, Username createdBy);

  Page<Offer> findVisibleTo(Username user, PageRequest pageRequest);

  boolean existsById(OfferId id);
}
