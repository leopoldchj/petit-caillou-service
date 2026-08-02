# petit-caillou-service

Application service for Petit Caillou: Java 21, Spring Boot 4.1, Hibernate 7
via Spring Data JPA, Flyway migrations, MariaDB 10.11.

## Configuration

All environment-specific settings come from a `.env` file at the root of this
project, which git ignores. Copy the template and fill it in:

```bash
cp .env.example .env
```

| Variable      | Purpose                          |
| ------------- | -------------------------------- |
| `DB_HOST`     | MariaDB host                     |
| `DB_PORT`     | MariaDB port                     |
| `DB_NAME`     | Database name                    |
| `DB_USERNAME` | Database user                    |
| `DB_PASSWORD` | Database password                |
| `SERVER_PORT` | HTTP port exposed by the service |

Spring Boot imports `.env` through `spring.config.import`. In CI or production,
supply the same keys as real environment variables and ship no `.env` at all.

## Database

The target server runs MariaDB 10.11. To get the same version locally:

```bash
docker compose up -d
```

It reads `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` and `DB_PORT` from your `.env`,
so both sides stay in sync.

## Run

```bash
./mvnw spring-boot:run
```

The service listens on `http://localhost:8080` and exposes `/actuator/health`.

The web application reaches it through the Vite dev proxy: a request to
`/api/...` on `http://localhost:5173` is forwarded to port 8080. The browser
sees a single origin, so there is no CORS configuration anywhere. Mirror that
with a reverse proxy rule in nginx when deploying.

## Test

```bash
./mvnw verify
```

Tests boot the application against a throwaway MariaDB 10.11 started by
Testcontainers, so they need a running Docker daemon and no local database.

## Schema changes

Flyway owns the schema and Hibernate runs with `ddl-auto: validate`. Adding a
persistent field is therefore always two steps:

1. Write the migration in `src/main/resources/db/migration`, named
   `V<version>__<description>.sql`.
2. Write or update the matching `@Entity`.

If the two disagree, the application refuses to start. That is intended: it
turns a silent schema drift into an immediate failure.
