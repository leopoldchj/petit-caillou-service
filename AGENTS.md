# Project agent instructions

## Backend service

- This repository contains the Petit Caillou backend service.

## Architecture

- Structure the project around a domain-centric architecture (Domain-Driven Design / hexagonal). The domain is the core and must not depend on frameworks, persistence, or transport concerns.
- Use four layers, each its own top-level package under `com.petitcaillou`:
  - `domain`: business classes only (entities, value objects, ports, domain exceptions). No Spring, JPA, or web annotations.
  - `service`: orchestration of domain classes. Framework-free; depends only on `domain` classes and ports. Name these classes `*Service`, grouped by business domain (for example `AuthService`, `UserService`).
  - `application`: the REST API (HTTP adapters, DTOs, HTTP error handling). Name the REST classes `*Resource` (for example `AuthResource`, `AccountResource`). Do not call them `Controller`, and do not call them `Service` (that term belongs to the `service` layer).
  - `infra`: technical adapters that implement the domain ports (persistence, security, configuration, DI wiring).
- A REST class must never fetch or produce data itself: it always delegates to a `service`. No repository access, no token minting, no business logic in the `application` layer — only request/response mapping and a call to a `*Service`.
- Organize `domain` by business concept, not by technical kind (for example `domain/user`, `domain/authentication`). Do not create a flat `model` package that lumps unrelated types together.
- Keep each business concept's exceptions in an `exceptions` subpackage (for example `domain/user/exceptions`, `domain/authentication/exceptions`).
- Define ports (interfaces) in the `domain` layer next to the business concept they serve; implement them in `infra`. Dependencies always point inward: `infra` and `application` depend on `service` and `domain`, never the reverse.
- Keep `domain` and `service` free of Spring. Since they carry no stereotype annotations, wire them as `@Bean` factories in an `infra` configuration class (the composition root).

## Authentication in APIs

- Endpoints are authenticated by default (`anyRequest().authenticated()` in the security config). Only add a path to the public allow-list when it must be reachable without a token.
- To read the authenticated user in a controller, take a `@CurrentUser AuthenticatedUser` parameter. Never inject Spring's `Jwt` (or `Authentication`) into `application` or `service` code; the `infra` resolver is the only place that touches `Jwt`.

## Design principles

- Carefully respect all SOLID principles:
  - Single Responsibility: each class has one reason to change.
  - Open/Closed: extend behavior without modifying existing code.
  - Liskov Substitution: subtypes must be usable through their base type without surprises.
  - Interface Segregation: prefer small, focused interfaces over large ones.
  - Dependency Inversion: depend on abstractions (ports), not concrete implementations.
- Apply "Tell, Don't Ask": tell objects what to do instead of querying their state and deciding for them. Keep behavior and the data it needs together to preserve domain encapsulation.

## Testing and TDD

- Always write tested code. Follow Test-Driven Development: write a failing test first, make it pass with the simplest code, then refactor.
- Structure every test with the AAA pattern (Arrange, Act, Assert).
- Name tests with the `given_when_then` convention, for example `given_emptyCart_when_addingItem_then_cartContainsItem`.
- Keep exactly one assertion or one `verify` per test.
- Each test must have a single reason to fail.
- Never use `try`/`catch` in a test. Assert expected exceptions with `assertThatThrownBy` (AssertJ) or `assertThrows` (JUnit).
- When several tests use the same value, or values that can be the same, extract it into a `private static final` class field instead of repeating the literal.
- Aim for high test coverage. Coverage is measured with JaCoCo (`mvn test` produces the report under `target/site/jacoco`). Cover the `domain`, `service`, `application`, and `infra` logic with fast unit tests; the Spring wiring classes (configuration, security setup, application entry point) are exercised by the Testcontainers integration test that boots the context.

## Code style

- Keep comments, documentation, identifiers, and developer-facing messages in English.
- Do not write comments in code unless a piece of logic is genuinely, unavoidably ambiguous. Prefer self-explanatory names and small methods over explanatory comments.
