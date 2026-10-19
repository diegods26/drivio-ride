# ADR-0001: Start With a Modular Monolith

- Status: Accepted
- Date: 2026-10-08

## Context

The planning document describes several capabilities that could eventually run
as services, but the current team and codebase are at the foundation stage.
Starting with independently deployed services would add network, deployment,
observability, and data-consistency costs before those needs are demonstrated.

## Decision

Deploy the initial backend as a modular monolith. Keep IAM, passenger, driver,
ride, location/matching, payment, notification, and administration as explicit
business modules with enforced dependency boundaries. Extract a module only
when there is a measured operational or organizational reason.

## Consequences

- One deployment and a shared transactional database simplify the MVP.
- Module boundaries, ports, and domain events preserve future extraction paths.
- Cross-module table access and framework leakage into domain code are not
  acceptable shortcuts.
- A modular monolith does not by itself provide service-level isolation or
  independent scaling; those become explicit trade-offs if extraction is needed.