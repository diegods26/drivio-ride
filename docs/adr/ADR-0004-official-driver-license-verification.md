# ADR-0004: Verify Driver Licenses Through an Authorized Source

- Status: Accepted, provider selection pending
- Date: 2026-10-08

## Context

Driver registration requires a valid CPF and a valid driver's license. A
syntactic check of a license number cannot prove that the issuing authority
currently considers it valid. The planning and product requirements require
verification with the traffic authority, while access to government systems
may be restricted and may vary by jurisdiction.

## Decision

Before driver approval or online eligibility, obtain a positive validity result
from an official traffic authority integration or a provider formally
authorized to verify on its behalf. Do not scrape public sites, infer validity
from document format, or treat a timeout, missing response, or provider outage
as success. Keep verification behind an outbound application port and isolate
provider credentials and protocol in an infrastructure adapter.

Until the official/authorized API and credentials are available, an
administrator may manually consult the issuing authority and record a positive
or negative review, the validity date when positive, a reason, reviewer ID,
and timestamp. The review endpoint records the administrator's attestation and
does not accept raw license numbers or document images; evidence is consulted
through the approved out-of-band process until secure document intake/storage
is designed and approved. The specific authority/provider, API contract, authorization
process, consent and legal basis, data-retention period, and
retry/reverification schedule must be confirmed before implementing the
electronic verification adapter.

## Consequences

- Driver approval is blocked until an authorized electronic check or documented
  administrator review confirms the license is valid and current.
- Vehicles remain inactive until a separate administrator review. Both vehicle
  activation and driver approval require an authenticated administrator and
  retain approver ID plus decision timestamp.
- Provider outages do not result in automatic approval. Manual administrator
  review is the interim path; absent either a successful electronic result or
  a recorded manual review, the driver remains blocked.
- Store only the minimum status, checked-at time, source, and audit reference
  required to justify the decision; do not log or retain unnecessary document
  contents.
- Adapter contract tests and domain tests must cover valid, expired, invalid,
  inconclusive, timeout, and unavailable results.