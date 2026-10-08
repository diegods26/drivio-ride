# Domain Boundaries and Business Rules

## Bounded contexts

| Context | Classification | Responsibility |
| --- | --- | --- |
| Ride | Core | Request, lifecycle, cancellation, history, and ride outcomes |
| Driver | Supporting | Onboarding, documents, approval, vehicle, availability, earnings |
| Passenger | Supporting | Passenger profile, contacts, addresses, and ride-facing preferences |
| Location and Matching | Core capability | Driver location, proximity search, eligibility, and assignment |
| Payment | Supporting | Post-ride collection, refunds, receipts, and payment state |
| Notification | Supporting | Email, push, SMS, and real-time user notifications |
| IAM | Generic | Identity, credentials, authentication, and authorization |
| Administration | Supporting | Operational workflows, moderation, and audit |

These are logical modules in one deployable application initially, not a
prescription to create one microservice per context.

## Core domain rules

1. A ride requires an authenticated, active passenger.
2. A driver may accept a ride only when approved, online, unblocked, and backed
   by an active vehicle.
3. A driver may serve at most one active ride; a passenger may own at most one
   active ride.
4. A ride cannot start before the driver has arrived, and cannot finish before
   it starts.
5. A completed ride is terminal and cannot transition to an earlier state.
6. Payment occurs only after ride completion.
7. A blocked driver cannot become available; a blocked user cannot request a
   ride.
8. Driver location is shared with a passenger only during the relevant active
   ride and only with authorized participants.
9. Fare estimation is shown before ride request. Final fare changes are limited
   to explicit, documented business conditions.
10. Cancellation is allowed only under role-specific lifecycle rules; fees and
    automatic expiry policy require an explicit product decision.
11. Every passenger and driver must provide a valid Brazilian CPF at
    registration. Validation includes length, repeated-digit rejection, and
    both check digits. CPF uniqueness is enforced transactionally by the
    database as well as by the application.
12. Client input never determines whether an account is an administrator or
    whether a driver is approved.
13. A driver is not eligible for approval or online status until the driver's
   license is verified as valid with an official traffic authority or a
   formally authorized verification provider. Missing, expired, inconclusive,
   or unavailable verification fails closed.

## Ride lifecycle baseline

The ride aggregate owns allowed transitions. The first implementation should
model transitions explicitly and reject invalid transitions in the domain,
independent of HTTP or persistence frameworks. Candidate states are:

`REQUESTED -> MATCHING -> ACCEPTED -> DRIVER_ARRIVED -> IN_PROGRESS -> COMPLETED`

Terminal alternatives include `CANCELLED`, `EXPIRED`, and `FAILED`. Exact
transition rules, cancellation fees, expiry timeouts, and payment-failure
recovery must be covered by tests and documented before implementing the ride
use cases.

## Personal-data handling

CPF is sensitive personal data. Persist the normalized digits required for
identity and uniqueness, restrict reads to justified use cases, never include
CPF in logs, and do not return it by default. A migration must not invent a
placeholder CPF to make existing rows pass validation; legacy records require
an explicit, safe remediation policy.

Driver license data and verification responses are also sensitive. Request the
driver's consent and establish the applicable legal basis before verification;
use only official/authorized channels, retain the minimum verification result
and audit metadata needed for the decision, and never log document numbers or
raw document images. A timeout or provider error must not be interpreted as a
valid license.