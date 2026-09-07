package com.petitcaillou.application;

import com.petitcaillou.domain.pagination.PageRequest;

final class PageParams
{
  private PageParams()
  {
  }

  static PageRequest of(Integer page, Integer size)
  {
    int resolvedPage = page == null || page < 0 ? 0 : page;
    int resolvedSize = size == null || size < 1
      ? PaginationDefaults.DEFAULT_SIZE
      : Math.min(size, PaginationDefaults.MAX_SIZE);
    return new PageRequest(resolvedPage, resolvedSize);
  }
}
