BEGIN;

INSERT INTO card_products (bank_id, name, card_type, active)
VALUES
    (2, 'ICICI Platinum Credit Card', 'CREDIT', TRUE),
    (2, 'ICICI Coral Credit Card', 'CREDIT', TRUE),
    (2, 'ICICI Amazon Pay Credit Card', 'CREDIT', TRUE),

    (3, 'HDFC Regalia Credit Card', 'CREDIT', TRUE),
    (3, 'HDFC Millennia Credit Card', 'CREDIT', TRUE),
    (3, 'HDFC Diners Club Privilege Credit Card', 'CREDIT', TRUE);

COMMIT;