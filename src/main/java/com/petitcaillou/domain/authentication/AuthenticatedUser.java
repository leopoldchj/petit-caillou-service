package com.petitcaillou.domain.authentication;

import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;

public record AuthenticatedUser(Alias alias, Email email, Role role)
{
  public AuthenticatedUser
  {
    if (alias == null || email == null || role == null)
    {
      throw new IllegalArgumentException("Authenticated user requires alias, email and role");
    }
  }
}
