package com.petitcaillou.service;

import com.petitcaillou.domain.user.Alias;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.domain.user.UserRepository;
import com.petitcaillou.domain.user.exceptions.UserNotFoundException;

public class UserService
{
  private final UserRepository users;

  public UserService(UserRepository users)
  {
    this.users = users;
  }

  public User byAlias(Alias alias)
  {
    return users.findByAlias(alias)
      .orElseThrow(UserNotFoundException::new);
  }
}
