package com.petitcaillou.application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.petitcaillou.domain.jobapplication.ResponseStatus;

public record JobApplicationUpdateRequest(
  LocalDate applicationDate,
  ResponseStatus responseStatus,
  @Size(max = 2000) String notes)
{
}
