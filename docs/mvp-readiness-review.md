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

- **Database provisioning:** Flyway now runs a cumulative B8 schema baseline
  and V9 constraint migration; Hibernate schema validation remains enabled.
  The local database was baselined at 8, migrated to 9, and passed schema
  validation. Existing V1–V8 fragments remain outside Flyway's runtime path.
  Fresh-database execution and baselining of hosted environments still need
  validation. Automatic baselining is off by default; see
  [database-migration-plan.md](database-migration-plan.md).
- **Authentication abuse protection:** the login API now limits each remote
  address to 10 requests per 15-minute window and returns HTTP 429 with a
  `Retry-After` header. This limiter is held in process memory, resets when the
  backend restarts, and uses the servlet remote address. Before hosting behind
  a reverse proxy or running multiple instances, configure trusted client-IP
  forwarding and enforce a shared limit at the gateway or shared store.
- **Account lifecycle:** password recovery and email verification remain
  backlog items. Decide whether the first hosted release requires them.
- **Deployment instructions:** the root README is a project stub, and there is
  no Dockerfile or compose deployment definition. Document required runtime
  configuration, database setup, and frontend origin before deployment.
- **Browser-token hardening:** the frontend stores the JWT in local storage.
  Review the XSS protections and token-storage approach before exposing the
  application to untrusted users.
- **Responsive QA:** the supplied screenshots confirm desktop Product and
  Best Deal layouts. Primary navigation now remains available in a horizontally
  scrollable row on narrow screens. Capture mobile layouts and the saved-Link
  panel before closing UX review.

## Recommended sequence

1. Finish mobile and saved-Link visual review.
2. Validate B8 and V9 against a disposable empty database, then baseline each
   hosted existing database at version 8 only after verifying and backing up
   its schema.
3. Decide the minimum account-recovery and email-verification scope for hosted
   onboarding; replace or complement the in-process login limit with shared
   enforcement when the hosting architecture is selected.
4. Complete deployment instructions and verify configuration against the
   intended hosting environment.
5. Revisit provider selection for LINK-14 and DATA-01 only after the MVP
   readiness review is closed.
