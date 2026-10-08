# Delivery and Quality

## Gitflow

- `main` contains releasable code.
- `develop` is the integration branch; it tracks `origin/develop` locally.
- Create `feature/<phase-or-story>` from `develop` for each approved phase.
- Validate and commit the completed phase on its feature branch, then request
  review/approval before integrating it into `develop`.
- Use `release/*` and `hotfix/*` only when release and production support are
  actually introduced.
- Do not force-push shared branches or include unrelated files in a phase
  commit. The planning PDF remains a source artifact and is not included in code
  commits unless explicitly requested.

The existing authentication work was committed on `feat/authentication` and
merged into local `develop` before this foundation branch was created. The
remote feature branch has not been pushed or modified by this workflow.

## Commit convention

Use Conventional Commits, for example:

- `feat(driver): add driver onboarding`
- `fix(ride): reject invalid lifecycle transition`
- `test(iam): cover invalid CPF registration`
- `docs(architecture): record modular monolith decision`

Keep commits focused on one coherent change and do not commit secrets, local
database files, generated output, or test credentials.

## Definition of done

- Acceptance criteria and relevant domain invariants are covered by tests.
- Maven verification and the CI workflow pass.
- API/OpenAPI and architecture documentation are updated when behavior changes.
- Security/privacy effects, migrations, idempotency, and authorization are
  reviewed for the change.
- Relevant domain-rule coverage is measured, with a target of at least 80%.
- No known critical dependency vulnerability is accepted without an explicit,
  documented risk decision.
- The feature commit is on its dedicated branch and is presented for review
  before the next phase starts.

## CI baseline

`.github/workflows/backend-ci.yml` runs the Maven `verify` lifecycle on pull
requests to `develop` and `main`, using JDK 21 and a PostgreSQL 17 service. The
workflow has read-only repository permissions and pins third-party actions to
immutable commit SHAs. Additional quality gates will be added with explicit
baselines rather than failing unpredictably on unrelated legacy issues.