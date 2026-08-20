package com.petitcaillou.domain.company.exceptions;

public class CompanyInUseException extends RuntimeException
{
  public CompanyInUseException()
  {
    super("Company is still referenced by job applications");
  }
}
