package com.petitcaillou.application.dto;

import java.util.List;
import java.util.function.Function;

import com.petitcaillou.domain.pagination.Page;

public record PageView<T>(
  List<T> items,
  int page,
  int size,
  long totalElements,
  int totalPages)
{
  public static <D, T> PageView<T> from(Page<D> page, Function<D, T> mapper)
  {
    return new PageView<>(
      page.items().stream().map(mapper).toList(),
      page.page(),
      page.size(),
      page.totalElements(),
      page.totalPages());
  }
}
