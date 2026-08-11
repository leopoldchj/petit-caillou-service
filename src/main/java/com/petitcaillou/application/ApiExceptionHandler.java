package com.petitcaillou.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.petitcaillou.domain.authentication.exceptions.InvalidCredentialsException;
import com.petitcaillou.domain.company.exceptions.CompanyNameAlreadyUsedException;
import com.petitcaillou.domain.company.exceptions.CompanyNotFoundException;
import com.petitcaillou.domain.jobapplication.exceptions.JobApplicationNotFoundException;
import com.petitcaillou.domain.user.exceptions.UsernameAlreadyUsedException;
import com.petitcaillou.domain.user.exceptions.EmailAlreadyUsedException;
import com.petitcaillou.domain.user.exceptions.WeakPasswordException;
import com.petitcaillou.domain.user.exceptions.UserNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler
{
  @ExceptionHandler(UsernameAlreadyUsedException.class)
  public ProblemDetail onUsernameAlreadyUsed(UsernameAlreadyUsedException exception)
  {
    return problem(HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler(EmailAlreadyUsedException.class)
  public ProblemDetail onEmailAlreadyUsed(EmailAlreadyUsedException exception)
  {
    return problem(HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ProblemDetail onInvalidCredentials(InvalidCredentialsException exception)
  {
    return problem(HttpStatus.UNAUTHORIZED, exception.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ProblemDetail onUserNotFound(UserNotFoundException exception)
  {
    return problem(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(JobApplicationNotFoundException.class)
  public ProblemDetail onJobApplicationNotFound(JobApplicationNotFoundException exception)
  {
    return problem(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(CompanyNotFoundException.class)
  public ProblemDetail onCompanyNotFound(CompanyNotFoundException exception)
  {
    return problem(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(CompanyNameAlreadyUsedException.class)
  public ProblemDetail onCompanyNameAlreadyUsed(CompanyNameAlreadyUsedException exception)
  {
    ProblemDetail detail = problem(HttpStatus.CONFLICT, exception.getMessage());
    detail.setProperty("existingCompanyId", exception.existingId().value().toString());
    return detail;
  }

  @ExceptionHandler(WeakPasswordException.class)
  public ProblemDetail onWeakPassword(WeakPasswordException exception)
  {
    return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail onInvalidBody(MethodArgumentNotValidException exception)
  {
    Map<String, String> errors = new HashMap<>();
    for (FieldError fieldError : exception.getBindingResult().getFieldErrors())
    {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Request validation failed");
    detail.setProperty("errors", errors);
    return detail;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail onUnreadableBody(HttpMessageNotReadableException exception)
  {
    return problem(HttpStatus.BAD_REQUEST, "Malformed request body");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail onInvalidArgument(IllegalArgumentException exception)
  {
    return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  private ProblemDetail problem(HttpStatus status, String detail)
  {
    return ProblemDetail.forStatusAndDetail(status, detail);
  }
}
