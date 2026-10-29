# LinkVault deployment guide

This guide deploys LinkVault as a single-host Docker Compose stack: PostgreSQL
with a persistent volume, a non-root Spring Boot API container, and an Nginx
container serving the React build and proxying `/api` requests to the backend.

## Requirements

- Docker Engine or Docker Desktop with the Compose plugin and Buildx
- OpenSSL for creating the application secrets
- Ports available for the configured `APP_PORT` (8080 by default)

## Fresh deployment

1. Copy `.env.example` to `.env` in the repository root.
2. Generate two separate values:

   ```text
   openssl rand -hex 24
   openssl rand -base64 32
   ```

   Put the first value in `DB_PASSWORD` and the second in `JWT_SECRET`. The
   JWT value must remain Base64 encoded and decode to at least 32 bytes. Keep
   `.env` private; it is excluded from Git.
3. Set `APP_CORS_ALLOWED_ORIGINS` to the exact browser origin. The local default
   is `http://localhost:8080`.
4. Start the services:

   ```text
   docker compose up --build -d
   ```

5. Check that all three services report healthy with `docker compose ps`, then
   open `http://localhost:8080`.

The frontend image is built with `/api` as its API base URL. Nginx serves
client-side routes through `index.html` and proxies API requests to the backend.
Compose waits for the backend health check before starting the frontend. Only
the frontend port is published; PostgreSQL and the API remain on the Compose
network. Nginx replaces forwarded-address headers with the address it received,
and Spring uses those headers for the request scheme and remote address.
Nginx also limits scripts to same-origin assets, allows HTTPS product images,
and sets frame and permissions policies.

The first startup against a new empty PostgreSQL volume applies Flyway B8 and
V9 before Hibernate validates the schema. Do not enable
`SPRING_FLYWAY_BASELINE_ON_MIGRATE` for a fresh database.

The backend exposes `/healthz`, which returns only an HTTP success status when
the application is ready. Compose uses it to wait for API startup before making
the frontend available.

## Validation checkpoint

On 30 October 2026, both container images built successfully and a disposable
Compose stack started against an empty PostgreSQL volume. PostgreSQL, backend,
and frontend health checks passed, Flyway applied B8 and V9, and the Spring
Boot application initialized its JPA entity manager. An unauthenticated request to
`/api/users/me` through Nginx returned HTTP 401 as expected. The disposable
stack and its volume were removed after the check. This confirms local
container startup; it does not verify a public host, TLS, or browser sign-in.

## Updates and database data

- Update images and restart with `docker compose up --build -d`.
- `docker compose down` stops the stack and keeps the database volume.
- Back up the database before deployments that change schema or application
  behavior. Store backups away from the host running the database.
- `docker compose down -v` deletes the PostgreSQL volume and all stored data;
  use it only when intentionally discarding the database.
- If deploying against an existing database, inspect and back it up first.
  Confirm its schema matches B8, set
  `SPRING_FLYWAY_BASELINE_ON_MIGRATE=true` for one startup, then return it to
  `false`. Never baseline an unverified schema.

## Public hosting

The Compose port binds to `127.0.0.1` by default. Terminate HTTPS at a trusted
reverse proxy and forward traffic to that local port. Set
`APP_CORS_ALLOWED_ORIGINS` to the public HTTPS origin. If the reverse proxy is
on another host or container, adjust the bind address and firewall so only the
trusted proxy can reach the app.

When another proxy sits in front of the included Nginx, configure Nginx's real
client-IP handling for only that trusted proxy range. The included login
limiter uses the resolved remote address; it is in process memory and resets
on restart. Multiple backend instances need a shared rate limiter.

## Current release limitations

- Password recovery and email verification are not implemented.
- Login throttling is per-process; it is not a shared multi-instance control.
- Browser authentication stores the JWT in local storage. The production
  Content Security Policy limits script sources, but scripts executing in the
  app origin can still read the token; review an HttpOnly-cookie option before
  broad public use.
- HTTPS, DNS, firewall policy, database backup scheduling, and monitoring must
  be provided by the hosting environment.

## Build without Buildx

The standard `docker compose up --build -d` command uses Docker Buildx. If
Buildx is missing, build both images with Docker's legacy builder in
PowerShell, then start Compose without rebuilding:

```powershell
$env:DOCKER_BUILDKIT = "0"
docker build -t linkvault-backend:local ./backend
docker build -t linkvault-frontend:local ./frontend
Remove-Item Env:DOCKER_BUILDKIT
docker compose up -d --no-build
```

Enable or install Buildx to use the standard Compose build command on later
updates.

Track remaining readiness tasks in [mvp-readiness-review.md](mvp-readiness-review.md).
