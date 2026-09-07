package com.petitcaillou.application;

import com.petitcaillou.domain.pagination.PageRequest;

public final class PaginationDefaults
{
  public static final int DEFAULT_SIZE = 20;
  public static final int MAX_SIZE = PageRequest.MAX_SIZE;

  private PaginationDefaults()
  {
  }
}
