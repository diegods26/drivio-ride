# Drivio Ride

Drivio Ride is a modular-monolith platform for urban mobility. The initial MVP
focuses on passenger and driver journeys, with business capabilities kept in
independent modules so they can evolve without premature service distribution.

## Repository

- `backend/drivio-core`: Java 21 and Spring Boot backend.
- `frontend`: frontend applications, planned after the backend is complete.
- `infra`: infrastructure assets.
- `docs`: product, architecture, security, delivery decisions, and the planning
  source document.

## Run the backend locally

Start PostgreSQL from the backend project directory:

```powershell
cd backend/drivio-core
docker compose up -d postgres
./mvnw.cmd test
```

The Compose credentials are for local development only. Do not reuse them in
shared, staging, or production environments. Application secrets must be
injected through the deployment environment or a secret manager.

## Delivery

The backend CI runs the Maven verification lifecycle against PostgreSQL on pull
requests targeting `develop` or `main`. See [docs/05-delivery-and-quality.md](docs/05-delivery-and-quality.md)
for Gitflow, commit conventions, and the definition of done.

## Documentation

Start at [docs/README.md](docs/README.md) for the product and architecture
baseline extracted from the project planning document.