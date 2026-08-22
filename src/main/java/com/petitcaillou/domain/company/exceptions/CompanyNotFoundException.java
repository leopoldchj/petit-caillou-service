package com.petitcaillou.domain.company.exceptions;

public class CompanyNotFoundException extends RuntimeException
{
  public CompanyNotFoundException()
  {
    super("Company does not exist");
  }
}
