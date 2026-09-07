package com.petitcaillou.application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OfferRequest(
  @NotBlank String companyId,
  @NotBlank @Size(max = 255) String title,
  @Size(max = 255) String location,
  @NotNull LocalDate publicationDate,
  @Size(max = 2048) String link,
  @Size(max = 4000) String description)
{
}
