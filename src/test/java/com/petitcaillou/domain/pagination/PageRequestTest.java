package com.petitcaillou.domain.pagination;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageRequestTest
{
  @Test
  void given_pageAndSize_when_computingOffset_then_multipliesThem()
  {
    assertThat(new PageRequest(2, 20).offset()).isEqualTo(40);
  }

  @Test
  void given_negativePage_when_creating_then_throws()
  {
    assertThatThrownBy(() -> new PageRequest(-1, 20))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_sizeAboveMax_when_creating_then_throws()
  {
    assertThatThrownBy(() -> new PageRequest(0, PageRequest.MAX_SIZE + 1))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_zeroSize_when_creating_then_throws()
  {
    assertThatThrownBy(() -> new PageRequest(0, 0))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
