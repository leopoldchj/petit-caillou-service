# Flyway migrations

Every schema change lives here as a versioned SQL file named
`V<version>__<description>.sql`, for example `V1__create_account_table.sql`.

Rules:

- A migration that has been applied is never edited. Correct it with a new one.
- Hibernate runs with `ddl-auto: validate`, so an entity without a matching
  migration fails the application at startup on purpose.
- The SQL targets MariaDB 10.11.
