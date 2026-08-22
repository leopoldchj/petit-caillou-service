package com.petitcaillou.domain.jobapplication;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JobApplicationIdTest
{
  @Test
  void given_nullValue_when_creating_then_rejectsIt()
  {
    assertThatThrownBy(() -> JobApplicationId.of(null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void given_sameUuid_when_comparingTwoIds_then_theyAreEqual()
  {
    UUID uuid = UUID.randomUUID();

    assertThat(JobApplicationId.of(uuid)).isEqualTo(JobApplicationId.of(uuid));
  }
}
