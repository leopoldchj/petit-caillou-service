package com.petitcaillou.domain.authentication;

import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.user.Email;
import com.petitcaillou.domain.user.Role;

public record AuthenticatedUser(Username username, Email email, Role role)
{
  public AuthenticatedUser
  {
    if (username == null || email == null || role == null)
    {
      throw new IllegalArgumentException("Authenticated user requires username, email and role");
    }
  }
}
