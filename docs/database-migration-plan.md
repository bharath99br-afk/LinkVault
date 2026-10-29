# Database migration plan

Updated: 29 October 2026

## Decision

Use Flyway as the canonical migration runner once LinkVault has a verified
baseline schema. Keep `spring.jpa.hibernate.ddl-auto=validate` so Hibernate
checks the mapped model without applying schema changes at application startup.
Flyway is not enabled yet: the current SQL files are incomplete and have not
been reconciled with the live database.

## Current repository state

- The backend has no Flyway or Liquibase dependency or runtime configuration.
- `V1__database_hardening.sql` is under `backend/database/migrations`, outside
  the application's resource directory.
- `V2__` through `V8__` are under
  `backend/src/main/java/com/linkvault/backend/database`. Maven compiles Java
  sources there; it does not package these SQL files as classpath resources.
- These files are incremental changes against pre-existing tables, not a
  complete schema for an empty database.
- `V3__card_catalog_seed.sql` assumes bank IDs 2 and 3. `V1__` also references
  a generated constraint name. Both assumptions need review against a verified
  database before those scripts can be replayed.
- No live database was inspected or changed during this review.

## Safe implementation sequence

1. Capture a schema-only dump from the authoritative database and record which
   V1–V8 changes are already present. Resolve the current schema and data
   differences before choosing a baseline version.
2. Create and review a complete baseline that provisions an empty PostgreSQL
   database. Treat existing databases separately: baseline only after the
   current schema has been verified to match the chosen version.
3. Reconcile the existing SQL fragments. Move only reviewed migrations into
   `backend/src/main/resources/db/migration`; replace fixed IDs with stable
   lookups or explicit seed keys where seed data is required.
4. Add Flyway and its PostgreSQL support, then validate migrations against a
   disposable empty database and a safe copy of the existing database.
5. Keep Hibernate schema validation enabled and document the migration command
   and backup/restore procedure for deployment.

Do not enable automatic migration or execute the existing scripts against the
local database until the schema and applied changes have been verified.
