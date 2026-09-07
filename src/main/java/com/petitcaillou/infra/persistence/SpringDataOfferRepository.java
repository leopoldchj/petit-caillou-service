package com.petitcaillou.infra.persistence;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataOfferRepository extends JpaRepository<OfferEntity, String>
{
  Optional<OfferEntity> findByContentHashAndCreatedBy(String contentHash, String createdBy);

  Page<OfferEntity> findByCreatedByIn(Collection<String> createdBy, Pageable pageable);

  boolean existsByCompanyId(String companyId);
}
