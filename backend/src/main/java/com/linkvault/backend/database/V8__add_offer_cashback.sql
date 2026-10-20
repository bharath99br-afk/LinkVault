
BEGIN;

ALTER TABLE offers
    ADD COLUMN cashback_percentage NUMERIC(5, 2);

ALTER TABLE offers
    ADD COLUMN max_cashback NUMERIC(12, 2);

COMMIT;
