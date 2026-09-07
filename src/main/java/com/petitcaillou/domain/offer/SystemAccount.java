package com.petitcaillou.domain.offer;

import com.petitcaillou.domain.user.Username;

public final class SystemAccount
{
  public static final Username USERNAME = Username.of("system");

  private SystemAccount()
  {
  }
}
