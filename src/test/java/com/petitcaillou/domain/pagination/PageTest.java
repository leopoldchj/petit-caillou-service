package com.petitcaillou.domain.pagination;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageTest
{
  @Test
  void given_totalAndSize_when_computingTotalPages_then_roundsUp()
  {
    Page<String> page = new Page<>(List.of("a"), 0, 20, 21);

    assertThat(page.totalPages()).isEqualTo(2);
  }

  @Test
  void given_mapper_when_mapping_then_transformsItemsAndKeepsMetadata()
  {
    Page<String> page = new Page<>(List.of("a", "bb"), 1, 20, 40);

    Page<Integer> mapped = page.map(String::length);

    assertThat(mapped.items()).containsExactly(1, 2);
  }

  @Test
  void given_mapper_when_mapping_then_keepsTotalElements()
  {
    Page<String> page = new Page<>(List.of("a"), 1, 20, 40);

    assertThat(page.map(String::length).totalElements()).isEqualTo(40);
  }
}
