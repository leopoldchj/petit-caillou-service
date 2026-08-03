package com.petitcaillou.application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petitcaillou.application.dto.CurrentUserView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.user.User;
import com.petitcaillou.service.UserService;

@RestController
public class AccountResource
{
  private final UserService userService;

  public AccountResource(UserService userService)
  {
    this.userService = userService;
  }

  @GetMapping("/me")
  public CurrentUserView me(@CurrentUser AuthenticatedUser current)
  {
    User user = userService.byAlias(current.alias());
    return new CurrentUserView(user.alias().value(), user.email().value(), user.role().name());
  }
}
