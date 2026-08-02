package com.petitcaillou.service;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the whole application against a throwaway MariaDB matching the server
 * version, which exercises the datasource, Flyway and Hibernate wiring together.
 */
@SpringBootTest
@Testcontainers
class PetitCaillouServiceApplicationTests
{
  @Container
  @ServiceConnection
  static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.11");

  @Autowired
  private DataSource dataSource;

  @Test
  void contextLoads()
  {
    assertThat(dataSource).isNotNull();
  }

  @Test
  void flywayCreatedItsSchemaHistory()
  {
    Integer tables = new JdbcTemplate(dataSource).queryForObject(
      "select count(*) from information_schema.tables "
        + "where table_schema = database() and table_name = 'flyway_schema_history'",
      Integer.class);

    assertThat(tables).isEqualTo(1);
  }
}
