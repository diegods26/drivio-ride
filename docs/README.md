# Drivio Ride Documentation

The planning source is `Project Drivio Planejamento.pdf`. The Markdown documents
below turn its product and architecture decisions into a maintainable baseline.
When a decision changes, update the relevant document and record significant
trade-offs in `adr/`.

## Baseline

- [Product vision and MVP](01-product-vision.md)
- [Domain boundaries and business rules](02-domain-and-business-rules.md)
- [Architecture](03-architecture.md)
- [Security baseline](04-security-baseline.md)
- [Delivery and quality](05-delivery-and-quality.md)
- [ADR-0001: Modular monolith](adr/ADR-0001-modular-monolith.md)
- [ADR-0002: Framework-independent domain](adr/ADR-0002-framework-independent-domain.md)
- [ADR-0003: Public-client OAuth security](adr/ADR-0003-public-client-oauth.md)

## Current implementation position

The backend currently contains the initial identity and access slice: user
registration, password authentication, CPF value-object validation, PostgreSQL
persistence, Flyway migrations, and an OAuth2 Authorization Server exercised
with PKCE integration tests. Passenger-specific profile capabilities and the
driver onboarding/approval lifecycle have not been implemented. The ride,
location, matching, payment, notification, administration, and observability
business capabilities remain planned.

The package directories for some planned contexts are placeholders, not
implemented features. The frontend is intentionally deferred until the backend
MVP has been completed and reviewed.

## Delivery phases

The roadmap follows the source plan while using a modular monolith as the first
deployment unit:

1. Foundation and architecture baseline.
2. Identity and access, including secure passenger/driver registration.
3. Passenger profile and account capabilities.
4. Driver onboarding, documents, vehicle, approval, and availability.
5. Location updates and privacy boundaries.
6. Ride lifecycle, pricing estimate, and ride history.
7. Driver matching and concurrency controls.
8. Simulated payment and payment events.
9. Notifications and ratings.
10. Administrative operations and audit trail.
11. Observability, resilience, CI/CD hardening, and production readiness.
12. Backend acceptance review; only then begin frontend planning.

Each phase is implemented on a dedicated feature branch, validated, committed,
and presented for review before the next phase begins.