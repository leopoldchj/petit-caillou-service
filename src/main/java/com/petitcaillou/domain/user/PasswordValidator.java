package com.petitcaillou.domain.user;

public interface PasswordValidator
{
  void validate(RawPassword password);
}
