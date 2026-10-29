# Database migration plan

Updated: 29 October 2026

## Decision

Use Flyway as the canonical migration runner and keep
`spring.jpa.hibernate.ddl-auto=validate` so Hibernate checks the mapped model
without changing the schema. Flyway uses `classpath:db/migration`; automatic
baselining of existing databases stays disabled by default.

`B8__baseline_schema.sql` is the cumulative schema for a new environment.
`V9__enforce_offer_card_product_constraints.sql` reconciles a missing
card-product constraint found in the existing local database. New migrations
start at V10.

## Current repository state

- Flyway is configured with `spring-boot-starter-flyway` and PostgreSQL support.
- Hibernate schema validation remains enabled.
- `V1__database_hardening.sql` is under `backend/database/migrations`, outside
  the application's resource directory.
- `V2__` through `V8__` are under
  `backend/src/main/java/com/linkvault/backend/database`. Maven compiles Java
  sources there; it does not package these SQL files as classpath resources.
- The old V1–V8 files are incremental changes against pre-existing tables, not
  a complete schema for an empty database, and remain outside Flyway's runtime
  location. Do not replay them.
- A read-only schema dump of local `linkvault` found 12 public tables and no
  Flyway history. The schema contained the V1, V2, V4, V6, V7, and V8 changes;
  it lacked the V5 unique index and had a nullable
  `offer_card_applicability.card_product_id` column.
- Before applying V9, the local data had zero null card-product IDs and zero
  duplicate offer/card-product pairs. Flyway baselined the database at version
  8, applied V9, and Hibernate schema validation passed. The normal web
  application then started successfully.
- No user rows were included in B8. The baseline inserts the two bank names and
  seven catalog card products used by the existing seed migrations.

## Safe implementation sequence

1. Validate B8 and V9 against a disposable empty PostgreSQL database; this has
   not been done yet.
2. For each existing environment, inspect and back up its schema, then baseline
   at version 8 only when its schema matches B8. Leave automatic baselining off.
3. Keep the historical V1–V8 fragments out of the runtime migration location;
   write all new changes as V10 or later.
4. Keep Hibernate schema validation enabled and document the migration and
   backup/restore procedure for each deployment environment.

For an existing database with a verified B8 schema and no Flyway history, set
`SPRING_FLYWAY_BASELINE_ON_MIGRATE=true` for one startup. Flyway uses the
configured baseline version 8, applies pending migrations, and then the
environment variable should be removed. The local development database has
already completed this step.
