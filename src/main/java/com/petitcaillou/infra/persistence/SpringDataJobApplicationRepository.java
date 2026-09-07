package com.petitcaillou.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataJobApplicationRepository extends JpaRepository<JobApplicationEntity, String>
{
  Page<JobApplicationEntity> findByOwnerUsername(String ownerUsername, Pageable pageable);

  boolean existsByOwnerUsernameAndOfferId(String ownerUsername, String offerId);
}
