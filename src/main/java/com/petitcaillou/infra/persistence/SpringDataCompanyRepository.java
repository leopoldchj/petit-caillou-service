package com.petitcaillou.infra.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCompanyRepository extends JpaRepository<CompanyEntity, String>
{
  Optional<CompanyEntity> findByNormalizedName(String normalizedName);

  List<CompanyEntity> findByNormalizedNameIn(Collection<String> normalizedNames);

  Page<CompanyEntity> findByNormalizedNameStartingWith(String normalizedPrefix, Pageable pageable);
}
