# LinkVault MVP readiness review

Updated: 30 October 2026

Current target: a deployable MVP by 31 October 2026, subject to closing the
remaining validation and hosting-specific items below.

This review separates the local MVP feature set from the requirements for a
hosted release. The container build and fresh-database startup were validated;
automated application tests were not run as part of this checkpoint.

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
- The `/healthz` endpoint is public and returns no application data; business
  API endpoints remain authenticated.
- CORS origins are configurable with `APP_CORS_ALLOWED_ORIGINS`; the default
  remains `http://localhost:5173` for local development.

## Open before a hosted release

- **Database provisioning:** Flyway now runs a cumulative B8 schema baseline
  and V9 constraint migration; Hibernate schema validation remains enabled.
  The local database was baselined at 8, migrated to 9, and passed schema
  validation. A disposable empty database also applied B8 and V9 successfully.
  Existing V1–V8 fragments remain outside Flyway's runtime path. Baseline any
  hosted existing database only after its schema is verified and backed up.
  Automatic baselining is off by default; see
  [database-migration-plan.md](database-migration-plan.md).
- **Authentication abuse protection:** the login API now limits each remote
  address to 10 requests per 15-minute window and returns HTTP 429 with a
  `Retry-After` header. This limiter is held in process memory, resets when the
  backend restarts, and uses the servlet remote address. Before hosting behind
  a reverse proxy or running multiple instances, configure trusted client-IP
  forwarding and enforce a shared limit at the gateway or shared store.
- **Account lifecycle:** password recovery and email verification remain
  backlog items. Decide whether the first hosted release requires them.
- **Deployment path:** Dockerfiles, Compose configuration, a secret template,
  Nginx API proxy, service health checks, and a deployment guide now exist.
  Compose config validates and both images build. A disposable fresh stack
  reached healthy states for all services; Flyway applied B8 and V9 to the
  empty database. The `/api` proxy returned the expected `401` for an
  unauthenticated request. This validates local container startup, not a public
  hosting environment.
- **Dependency audit:** the first container build found two high-severity
  development dependency advisories. The frontend lockfile was updated to
  compatible versions, the image rebuilt, and `npm ci` then reported no known
  vulnerabilities.
- **Browser-token hardening:** the production Nginx response now sets a Content
  Security Policy that limits scripts to same-origin files, plus frame and
  permissions restrictions. The frontend still stores the JWT in local
  storage, which remains readable by scripts running in the app origin. Review
  an HttpOnly-cookie option before exposing the application broadly.
- **Responsive QA:** the supplied screenshots confirm desktop Product and
  Best Deal layouts. Primary navigation now remains available in a horizontally
  scrollable row on narrow screens. Capture mobile layouts and the saved-Link
  panel before closing UX review.

## Recommended sequence

1. Finish mobile and saved-Link visual review.
2. Before hosting, verify the selected database schema and take a backup; only
   baseline a compatible existing schema at version 8.
3. Decide the minimum account-recovery and email-verification scope for hosted
   onboarding; replace or complement the in-process login limit with shared
   enforcement when the hosting architecture is selected.
4. Select a hosting environment and verify its secrets, TLS, proxy, database
   backup, and monitoring configuration.
5. Revisit provider selection for LINK-14 and DATA-01 only after the MVP
   readiness review is closed.
