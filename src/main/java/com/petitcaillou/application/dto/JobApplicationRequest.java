package com.petitcaillou.application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.petitcaillou.domain.jobapplication.ResponseStatus;

public record JobApplicationRequest(
  @Size(max = 2048) String link,
  @NotBlank String companyId,
  @NotBlank @Size(max = 255) String title,
  @Size(max = 2000) String description,
  @Size(max = 255) String location,
  LocalDate applicationDate,
  ResponseStatus responseStatus)
{
}
