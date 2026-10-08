# Security Baseline

## Identity and access

- Use OAuth2 Authorization Code with PKCE for browser/mobile public clients;
  never embed a client secret in a client application.
- Public client refresh-token behavior must follow the authorization server's
  protections (rotation/reuse detection or sender constraint). Do not enable a
  refresh grant merely to satisfy a response assertion.
- Enforce authorization at each use case and resource boundary, not only in the
  UI. Roles and driver approval state are assigned by trusted server workflows.
- Deny by default. Administrative endpoints require a dedicated privileged role
  and auditable actions.

## Credentials and sensitive data

- Store only adaptive password hashes (BCrypt or a stronger supported
  equivalent); never persist or log raw passwords.
- CPF must be valid and unique for passenger and driver registration. Normalize
  before validation and persistence. Avoid returning CPF or including it in
  logs, traces, metrics, errors, or analytics.
- Keep secrets out of source control and use a secret manager for non-local
  environments. Compose defaults are strictly local development credentials.
- Encrypt transport with TLS outside local development; restrict database
  network access and use least-privilege database credentials.

## API and operations

- Validate all external input at the boundary and enforce domain invariants in
  the domain/application layer.
- Use generic authentication failure responses to avoid account enumeration.
- Add rate limiting to registration, login, password recovery, and expensive
  ride/matching endpoints before production exposure.
- Record correlation IDs and security-relevant administrative actions without
  recording credentials, tokens, CPF, payment credentials, or precise location
  beyond an authorized operational need.
- Apply idempotency to payment and ride commands that can be retried.
- Review dependency vulnerabilities and container images in CI/release gates.

## Current limitations

The backend is not production-ready. Production secrets, TLS termination,
rate-limiting, durable token revocation, refresh-token lifecycle, audit storage,
dependency scanning, and operational monitoring are not yet complete. These
must be closed in their relevant backend phases before deployment.