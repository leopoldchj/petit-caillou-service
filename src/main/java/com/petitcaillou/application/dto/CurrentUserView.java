package com.petitcaillou.application.dto;

public record CurrentUserView(
  String alias,
  String email,
  String role)
{
}
