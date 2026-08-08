package com.petitcaillou.service;

import com.petitcaillou.domain.user.Username;
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

  public User byUsername(Username username)
  {
    return users.findByUsername(username)
      .orElseThrow(UserNotFoundException::new);
  }
}
