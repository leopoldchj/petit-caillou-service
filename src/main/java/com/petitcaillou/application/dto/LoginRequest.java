package com.petitcaillou.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
  @NotBlank String alias,
  @NotBlank String password)
{
}
