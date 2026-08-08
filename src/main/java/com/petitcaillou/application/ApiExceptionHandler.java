package com.petitcaillou.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.petitcaillou.domain.authentication.exceptions.InvalidCredentialsException;
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

  @ExceptionHandler(WeakPasswordException.class)
  public ProblemDetail onWeakPassword(WeakPasswordException exception)
  {
    return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
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
