BEGIN;

CREATE UNIQUE INDEX uk_offer_card_applicability_card_product
    ON offer_card_applicability (offer_id, card_product_id);

COMMIT;