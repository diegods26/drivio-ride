# Product Vision

## Mission

Drivio Ride connects passengers and drivers through a safe, reliable urban
mobility platform while demonstrating maintainable software architecture and
engineering practices.

## Users

- **Passenger:** registers, requests and tracks a ride, pays, reviews the
  experience, and manages their profile.
- **Driver:** registers, submits documents, registers a vehicle, awaits approval,
  becomes available, serves rides, and reviews earnings/history.
- **Administrator:** approves drivers, manages accounts and rides, and reviews
  operational information with an auditable trail.
- **External providers:** maps, payment, email, SMS, and push providers. MVP
  integrations may be simulated where specified.

## MVP scope

The MVP targets registration and login, passenger profile, driver and vehicle
onboarding, driver approval and availability, ride request through completion,
ride tracking, history and rating, simulated payment, notifications, and a basic
administrative workflow.

The planning document places deliveries, motorcycle rides, scheduled and shared
rides, loyalty, wallet, coupons, dynamic pricing, and multi-currency in the
post-MVP roadmap. These are not prerequisites for the first backend MVP unless
product review changes scope.

## Success criteria

- A complete ride can be requested, matched, accepted, started, completed,
  paid (simulated), and rated.
- Protected APIs require authenticated and authorized access.
- Passenger and driver registration requires a mathematically valid CPF.
- Business rules have focused automated tests; target at least 80% coverage of
  relevant domain rules.
- API documentation and architecture decisions remain current.
- CI passes and dependency scanning has no unresolved critical findings before
  release.

## Product constraints

- Complete the backend before beginning frontend implementation.
- Keep passenger and driver identities bound to server-controlled roles; clients
  cannot grant themselves privileged roles.
- Collect only personal data needed for the product, restrict access, and avoid
  exposing CPF in logs or ordinary API responses.