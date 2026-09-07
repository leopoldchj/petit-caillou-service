package com.petitcaillou.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.petitcaillou.domain.authentication.TokenIssuer;
import com.petitcaillou.domain.company.CompanyRepository;
import com.petitcaillou.domain.jobapplication.JobApplicationRepository;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.OfferSource;
import com.petitcaillou.domain.user.DefaultPasswordValidator;
import com.petitcaillou.domain.user.PasswordHasher;
import com.petitcaillou.domain.user.PasswordValidator;
import com.petitcaillou.domain.user.UserRepository;
import com.petitcaillou.service.AuthService;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.JobApplicationService;
import com.petitcaillou.service.OfferIngestionService;
import com.petitcaillou.service.OfferService;
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
  CompanyService companyService(CompanyRepository companies, OfferRepository offers)
  {
    return new CompanyService(companies, offers);
  }

  @Bean
  OfferService offerService(OfferRepository offers, CompanyRepository companies)
  {
    return new OfferService(offers, companies);
  }

  @Bean
  JobApplicationService jobApplicationService(JobApplicationRepository applications, OfferRepository offers)
  {
    return new JobApplicationService(applications, offers);
  }

  @Bean
  OfferIngestionService offerIngestionService(OfferSource offerSource, CompanyService companies, OfferService offers)
  {
    return new OfferIngestionService(offerSource, companies, offers);
  }
}
