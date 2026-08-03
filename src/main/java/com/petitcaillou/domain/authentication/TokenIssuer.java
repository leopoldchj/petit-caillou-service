package com.petitcaillou.domain.authentication;

import com.petitcaillou.domain.user.User;

public interface TokenIssuer
{
  AccessToken issueFor(User user);
}
