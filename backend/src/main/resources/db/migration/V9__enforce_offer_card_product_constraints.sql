DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM public.offer_card_applicability
        WHERE card_product_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'Cannot enforce offer card-product constraints: null card_product_id values exist';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM public.offer_card_applicability
        GROUP BY offer_id, card_product_id
        HAVING count(*) > 1
    ) THEN
        RAISE EXCEPTION
            'Cannot enforce offer card-product constraints: duplicate offer/card-product pairs exist';
    END IF;
END $$;

ALTER TABLE public.offer_card_applicability
    ALTER COLUMN card_product_id SET NOT NULL;

CREATE UNIQUE INDEX uk_offer_card_applicability_card_product
    ON public.offer_card_applicability (offer_id, card_product_id);
