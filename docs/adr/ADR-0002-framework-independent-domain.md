# ADR-0002: Keep the Domain Framework-Independent

- Status: Accepted
- Date: 2026-10-08

## Context

Business invariants must remain testable without starting Spring, a database, or
an external service. Framework annotations and persistence concerns can otherwise
shape the model around infrastructure rather than the business language.

## Decision

Domain entities, value objects, domain services, and domain events use plain
Java and do not depend on Spring, JPA/Hibernate, Jackson, Kafka, or Redis.
Application use cases depend on ports; infrastructure implements those ports.

## Consequences

- Domain tests remain fast and isolated.
- Persistence mapping and serialization belong in adapters.
- Cross-module references require stable public contracts rather than internal
  entity reuse.
- Framework integration tests remain necessary at adapter boundaries.