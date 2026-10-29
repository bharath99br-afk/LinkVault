# LinkVault MVP readiness review

Updated: 29 October 2026

This review separates the local MVP feature set from the requirements for a
hosted release. It is a source and documentation review; no tests were run as
part of this checkpoint.

## Current MVP position

- The Best Deal promise is confirmed for the current MVP: compare eligible
  offers for one purchase at one store, rank by effective price after
  estimated cashback, show checkout price separately, and disclose ties.
- The saved Product desktop flow and Best Deal result have been visually
  reviewed. The saved-Link flow is source-reviewed; its shared optimizer now
  uses neutral “saved page” prompts. Mobile and saved-Link screenshots remain
  open.
- Backend integration coverage exists for authentication, resource ownership,
  offer eligibility, cards, products, links, and Best Deal. The Best Deal suite
  most recently passed 28 tests before this review.
- Passwords use BCrypt. The API is stateless and uses short-lived JWTs; routes
  other than registration and login require authentication.
- The application validates the existing database schema at startup with
  `spring.jpa.hibernate.ddl-auto=validate`, and SQL logging and open-in-view
  are disabled.
- CORS origins are configurable with `APP_CORS_ALLOWED_ORIGINS`; the default
  remains `http://localhost:5173` for local development.

## Open before a hosted release

- **Database provisioning:** versioned SQL fragments V1–V8 exist, but no
  Flyway or Liquibase runner is configured and there is no complete baseline
  schema. V1 is outside the application resources; V2–V8 are under
  `src/main/java`, where Maven does not package them as runtime resources.
  Several scripts assume pre-existing tables, and V3 uses fixed bank IDs, so
  the fragments cannot provision a fresh database safely. The selected target
  is Flyway with Hibernate schema validation retained; see
  [database-migration-plan.md](database-migration-plan.md). Do not enable or
  replay the scripts automatically until a baseline is verified against the
  existing schema.
- **Authentication abuse protection:** no server-side login rate limiter was
  found. The frontend can display a 429 response, but that does not enforce a
  limit on the API.
- **Account lifecycle:** password recovery and email verification remain
  backlog items. Decide whether the first hosted release requires them.
- **Deployment instructions:** the root README is a project stub, and there is
  no Dockerfile or compose deployment definition. Document required runtime
  configuration, database setup, and frontend origin before deployment.
- **Browser-token hardening:** the frontend stores the JWT in local storage.
  Review the XSS protections and token-storage approach before exposing the
  application to untrusted users.
- **Responsive QA:** the supplied screenshots confirm desktop Product and
  Best Deal layouts. Capture mobile layouts and the saved-Link panel before
  closing UX review.

## Recommended sequence

1. Finish mobile and saved-Link visual review.
2. Capture and review the current schema, reconcile the V1–V8 SQL fragments,
   create a complete baseline, then enable Flyway and validate fresh and
   existing-database paths.
3. Add API-side login throttling and decide the minimum account-recovery and
   verification scope for hosted onboarding.
4. Complete deployment instructions and verify configuration against the
   intended hosting environment.
5. Revisit provider selection for LINK-14 and DATA-01 only after the MVP
   readiness review is closed.
