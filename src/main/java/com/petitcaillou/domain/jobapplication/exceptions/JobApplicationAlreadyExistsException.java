package com.petitcaillou.domain.jobapplication.exceptions;

public class JobApplicationAlreadyExistsException extends RuntimeException
{
  public JobApplicationAlreadyExistsException()
  {
    super("You have already applied to this offer");
  }
}
