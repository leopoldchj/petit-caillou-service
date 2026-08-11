package com.petitcaillou.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.petitcaillou.domain.authentication.TokenIssuer;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.user.DefaultPasswordValidator;
import com.petitcaillou.domain.user.PasswordHasher;
import com.petitcaillou.domain.user.PasswordValidator;
import com.petitcaillou.domain.user.UserRepository;
import com.petitcaillou.service.AuthService;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.JobApplicationService;
import com.petitcaillou.service.UserService;

@Configuration
public class ApplicationConfig
{
  @Bean
  PasswordValidator passwordValidator()
  {
    return new DefaultPasswordValidator();
  }

  @Bean
  AuthService authService(UserRepository users, PasswordValidator passwordValidator, PasswordHasher passwordHasher,
    TokenIssuer tokenIssuer)
  {
    return new AuthService(users, passwordValidator, passwordHasher, tokenIssuer);
  }

  @Bean
  UserService userService(UserRepository users)
  {
    return new UserService(users);
  }

  @Bean
  CompanyService companyService(CompanyRepository companies)
  {
    return new CompanyService(companies);
  }

  @Bean
  JobApplicationService jobApplicationService(JobApplicationRepository applications, CompanyRepository companies)
  {
    return new JobApplicationService(applications, companies);
  }
}
