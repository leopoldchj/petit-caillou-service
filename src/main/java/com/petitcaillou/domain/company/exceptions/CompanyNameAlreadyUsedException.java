package com.petitcaillou.domain.company.exceptions;

import com.petitcaillou.domain.company.CompanyId;

public class CompanyNameAlreadyUsedException extends RuntimeException
{
  private final CompanyId existingId;

  public CompanyNameAlreadyUsedException(CompanyId existingId)
  {
    super("Company name is already registered");
    this.existingId = existingId;
  }

  public CompanyId existingId()
  {
    return existingId;
  }
}
