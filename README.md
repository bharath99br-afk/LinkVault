# LinkVault

LinkVault is a purchase-decision app built with React, Spring Boot, and
PostgreSQL. It keeps saved products and payment offers together, then compares
eligible card offers for one purchase at one store.

## Run with Docker Compose

Docker Compose starts PostgreSQL, the Spring Boot API, and the React app behind
Nginx. See [the deployment guide](docs/deployment.md) for configuration,
database migration, backup, and production notes.

1. Copy `.env.example` to `.env`.
2. Set `DB_PASSWORD` and a Base64-encoded `JWT_SECRET` in `.env`. Generate
   values with `openssl rand -hex 24` and `openssl rand -base64 32`.
3. Start the stack with `docker compose up --build -d`.
4. Open `http://localhost:8080`.

The Compose port binds to `127.0.0.1` by default. Put a TLS-enabled reverse
proxy in front of it before allowing public access.
If your Docker installation does not include Buildx, see the fallback build
commands in the deployment guide.

## Local development

- Backend: `cd backend` then `./mvnw spring-boot:run` (PowerShell:
  `./mvnw.cmd spring-boot:run`). Configure `DB_PASSWORD` and `JWT_SECRET`.
- Frontend: `cd frontend`, `npm ci`, then `npm run dev`.
- The backend uses PostgreSQL and Flyway. Fresh databases apply the B8 baseline
  followed by V9; existing databases need a schema check before baselining.

## Project notes

- Java 21 and Maven Wrapper
- Node.js 22 and npm
- PostgreSQL 17
- Database migrations and MVP readiness: `docs/database-migration-plan.md` and
  `docs/mvp-readiness-review.md`
