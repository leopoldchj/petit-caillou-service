package com.petitcaillou.infra.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataUserRepository extends JpaRepository<UserEntity, String>
{
  boolean existsByAlias(String alias);

  boolean existsByEmail(String email);

  Optional<UserEntity> findByAlias(String alias);
}
