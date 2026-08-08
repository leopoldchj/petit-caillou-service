package com.petitcaillou.application.dto;

public record CurrentUserView(
  String username,
  String email,
  String role)
{
}
