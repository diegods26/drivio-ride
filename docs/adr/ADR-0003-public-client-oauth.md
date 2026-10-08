# ADR-0003: Protect OAuth Public Clients With PKCE

- Status: Accepted
- Date: 2026-10-08

## Context

Passenger and driver applications cannot safely keep a client secret. The
authorization server identifies the browser client as public and requires
Proof Key for Code Exchange (PKCE).

## Decision

Use Authorization Code with PKCE for public clients and do not issue a refresh
token unless the selected authorization-server configuration enforces rotation
with reuse detection or an equivalent sender-constrained mechanism. Do not
declare unsupported grants or weaken framework protections to make a test pass.

## Consequences

- Authorization requests and integration tests must send query parameters as
  actual URL query parameters.
- The client secret is never shipped to the frontend.
- A future refresh-token design requires an explicit threat review, lifecycle
  tests, revocation behavior, and secure storage decisions.
- The current public client receives an access token only; refresh-token
  lifecycle is not implemented.