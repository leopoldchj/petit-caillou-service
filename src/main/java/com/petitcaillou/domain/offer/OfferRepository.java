package com.petitcaillou.domain.offer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

public interface OfferRepository
{
  Offer save(Offer offer);

  Optional<Offer> findById(OfferId id);

  List<Offer> findAllByIds(Collection<OfferId> ids);

  boolean existsByCompany(CompanyId companyId);

  Optional<Offer> findByDedupKeyAndCreatedBy(ContentHash dedupKey, Username createdBy);

  Page<Offer> findVisibleTo(Username user, PageRequest pageRequest);

  boolean existsById(OfferId id);
}
