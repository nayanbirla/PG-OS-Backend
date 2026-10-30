## Part 1: Global context (put in `.github/copilot-instructions.md`)

PG OS is a PG (paying guest) and flat management platform plus a discovery marketplace.

**Stack:** Java 17, Spring Boot 3.x, Maven, Spring Web, Spring Security, Spring Data JPA, PostgreSQL, Flyway, Bean Validation, springdoc-openapi, Lombok, Testcontainers (PostgreSQL) + JUnit 5. Frontend (later): React. Packaged with Docker.

**Architecture:** modular monolith, package-by-feature under `com.pgos`:
`auth, user, property, booking, tenancy, payment, complaint, notice, admin, common`.
Inside each module: `controller, service, repository, entity, dto`.

**Rules for all code:**
- Controllers accept and return DTOs only. Never expose JPA entities.
- All primary and foreign keys are `BIGINT` (`Long`), generated with `GenerationType.IDENTITY`. No UUIDs.
- Schema is owned by Flyway migrations. Hibernate runs with `ddl-auto=validate`.
- Table name for users is `app_user` (`user` is reserved in PostgreSQL).
- All list endpoints are paginated (`Pageable`). Money is `NUMERIC(10,2)` / `BigDecimal`. Dates are `LocalDate`, timestamps are `Instant` (UTC).
- Constructor injection only. Use `@Transactional` on service methods that write.
- Errors go through one `@RestControllerAdvice` and return the standard error JSON (see Part 3).
- Every endpoint is authorized at two levels: role (`@PreAuthorize`) and resource scope (property ownership or membership, via the `access` bean). Never trust an id from the URL without checking scope.
- Property-scoped resources are always nested under `/api/v1/properties/{propertyId}/...` and the service must verify the child really belongs to that property.
- Write unit tests for service logic and Testcontainers integration tests for repositories and security rules.
- Do not add features, endpoints, or fields that are not in the spec.

---
