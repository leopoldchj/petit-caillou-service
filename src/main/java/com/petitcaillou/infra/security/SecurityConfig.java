package com.petitcaillou.infra.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig
{
  private final JwtProperties properties;

  public SecurityConfig(JwtProperties properties)
  {
    this.properties = properties;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
  {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(requests -> requests
        .requestMatchers("/auth/**", "/actuator/health/**").permitAll()
        .anyRequest().authenticated())
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> { }));

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder()
  {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtEncoder jwtEncoder()
  {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
  }

  @Bean
  JwtDecoder jwtDecoder()
  {
    return NimbusJwtDecoder.withSecretKey(secretKey()).build();
  }

  private SecretKey secretKey()
  {
    String secret = properties.getSecret();
    if (secret == null || secret.isBlank())
    {
      throw new IllegalStateException("JWT secret must be configured (petit-caillou.jwt.secret / JWT_SECRET)");
    }

    byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32)
    {
      throw new IllegalStateException("JWT secret must be at least 32 bytes");
    }

    return new SecretKeySpec(bytes, "HmacSHA256");
  }
}
