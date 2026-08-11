package com.petitcaillou.domain.jobapplication.exceptions;

public class JobApplicationNotFoundException extends RuntimeException
{
  public JobApplicationNotFoundException()
  {
    super("Job application does not exist");
  }
}
