package com.petitcaillou.infra.persistence;

import java.util.function.Function;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;

final class PageMapper
{
  private PageMapper()
  {
  }

  static Pageable toPageable(PageRequest pageRequest, Sort sort)
  {
    return org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size(), sort);
  }

  static <E, D> Page<D> toDomain(org.springframework.data.domain.Page<E> page, PageRequest pageRequest,
    Function<E, D> mapper)
  {
    return new Page<>(
      page.getContent().stream().map(mapper).toList(),
      pageRequest.page(),
      pageRequest.size(),
      page.getTotalElements());
  }
}
