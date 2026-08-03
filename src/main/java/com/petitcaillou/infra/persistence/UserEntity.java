package com.petitcaillou.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.petitcaillou.domain.user.Role;

@Entity
@Table(name = "users")
public class UserEntity
{
  @Id
  @Column(name = "alias", nullable = false, updatable = false, length = 30)
  private String alias;

  @Column(name = "email", nullable = false, unique = true, length = 320)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private Role role;

  protected UserEntity()
  {
  }

  public UserEntity(String alias, String email, String passwordHash, Role role)
  {
    this.alias = alias;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public String getAlias()
  {
    return alias;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPasswordHash()
  {
    return passwordHash;
  }

  public Role getRole()
  {
    return role;
  }
}
