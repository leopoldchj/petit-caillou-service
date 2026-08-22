package com.petitcaillou.infra.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataJobApplicationRepository extends JpaRepository<JobApplicationEntity, String>
{
  List<JobApplicationEntity> findByOwnerUsername(String ownerUsername);

  List<JobApplicationEntity> findByOwnerUsernameAndCompanyId(String ownerUsername, String companyId);

  boolean existsByCompanyId(String companyId);
}
