package com.petitcaillou.infra.persistence;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.petitcaillou.domain.pagination.Page;

final class PageMapper
{
  private PageMapper()
  {
  }

  static Pageable toPageable(int page, int size, Sort sort)
  {
    return PageRequest.of(page, size, sort);
  }

  static <D> Page<D> toDomain(List<D> content, int page, int size, long totalElements)
  {
    return new Page<>(content, page, size, totalElements);
  }
}
