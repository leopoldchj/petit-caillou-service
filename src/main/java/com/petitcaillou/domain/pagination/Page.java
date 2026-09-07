package com.petitcaillou.domain.pagination;

import java.util.List;

public record Page<T>(List<T> items, int page, int size, long totalElements)
{
  public int totalPages()
  {
    if (size == 0)
    {
      return 0;
    }
    return (int) Math.ceil((double) totalElements / size);
  }

  public <R> Page<R> map(java.util.function.Function<T, R> mapper)
  {
    return new Page<>(items.stream().map(mapper).toList(), page, size, totalElements);
  }
}
