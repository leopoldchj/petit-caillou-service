package com.petitcaillou.infra.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCompanyRepository extends JpaRepository<CompanyEntity, String>
{
  Optional<CompanyEntity> findByNormalizedName(String normalizedName);
}
