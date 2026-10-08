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
- A driver must not be approved or enabled for rides unless their driver's
  license has a positive, current result from an official traffic authority or
  formally authorized provider. No result, expired status, provider outage, or
  ambiguity is a denial, not an implicit approval.
- Driver-submitted vehicles start inactive. Only an authorized administrator
  can activate a vehicle or approve a driver; persist approver identity and
  timestamp for both decisions.
- Driver-license verification requires a documented legal basis/consent, a
  restricted integration credential, minimal retention, and auditable results.
  Never log license numbers, raw documents, or provider credentials.
- Interim manual CNH review stores only the decision, validity date, review
  reason, administrator ID, and timestamp. The review API must not accept raw
  CNH values or document images; evidence remains in the separately approved
  out-of-band process until secure document storage is designed.
- Passenger profile and saved-address operations derive the owner from the
  authenticated subject, require an active passenger account, and scope every
  address read/delete by both owner ID and resource ID. Never accept an owner ID
  from the request body or URL. Treat saved addresses and phone numbers as
  sensitive personal data and do not log them.
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