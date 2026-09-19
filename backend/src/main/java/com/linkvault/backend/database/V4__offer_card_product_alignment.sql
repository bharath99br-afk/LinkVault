BEGIN;

-- 1. Add canonical CardProduct reference to offer-card applicability.
ALTER TABLE offer_card_applicability
    ADD COLUMN card_product_id BIGINT;

-- 2. Ensure the existing HDFC Platinum card has a canonical
--    CardProduct representation.
INSERT INTO card_products (bank_id, name, card_type, active)
SELECT
    b.id,
    'HDFC Platinum Credit Card',
    'CREDIT',
    TRUE
FROM banks b
WHERE b.name = 'HDFC Bank'
  AND NOT EXISTS (
      SELECT 1
      FROM card_products cp
      WHERE cp.bank_id = b.id
        AND LOWER(cp.name) = LOWER('HDFC Platinum Credit Card')
  );

-- 3. Add the foreign-key relationship.
ALTER TABLE offer_card_applicability
    ADD CONSTRAINT fk_offer_card_card_product
    FOREIGN KEY (card_product_id)
    REFERENCES card_products(id);

-- 4. Backfill existing offer-card applicability
--    using the existing bank + card name relationship.
UPDATE offer_card_applicability oca
SET card_product_id = cp.id
FROM card_products cp
WHERE cp.bank_id = oca.bank_id
  AND LOWER(cp.name) = LOWER(oca.card_name);

-- 5. Backfill existing user cards wherever a canonical
--    CardProduct can be identified by bank + existing name.
UPDATE cards c
SET card_product_id = cp.id
FROM card_products cp
WHERE c.bank_id = cp.bank_id
  AND LOWER(c.name) = LOWER(cp.name)
  AND c.card_product_id IS NULL;

-- 6. Index the new relationship.
CREATE INDEX idx_offer_card_applicability_card_product_id
    ON offer_card_applicability(card_product_id);

COMMIT;