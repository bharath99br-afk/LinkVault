BEGIN;

-- Enforce application-level Link validation at the database layer
ALTER TABLE links
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE links
    ALTER COLUMN url SET NOT NULL;

-- Replace case-sensitive email uniqueness with case-insensitive uniqueness
ALTER TABLE users
    DROP CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7;

CREATE UNIQUE INDEX uk_users_email_lower
    ON users (LOWER(email));

-- Enforce case-insensitive uniqueness for global merchant names
CREATE UNIQUE INDEX uk_global_merchants_name_lower
    ON global_merchants (LOWER(name));

COMMIT;
