# Offer eligibility and calculation audit

Updated: 29 October 2026

This source review records how LinkVault currently qualifies and ranks card
offers. It documents implemented behavior; it does not change the product's
Best Deal promise or claim that cashback is guaranteed.

## Eligibility

| Check | Current behavior |
| --- | --- |
| User and card ownership | The eligibility service loads the card by both card ID and the authenticated user's ID. |
| Offer dates | The offer is active when today is on or after its start date and on or before its end date. |
| Minimum spend | A configured minimum is inclusive: the transaction qualifies when it is equal to or greater than the threshold. |
| Bank and card-product restrictions | An unrestricted offer matches any card. If restrictions exist, a matching bank **or** matching catalog card product qualifies. Catalog card-product ID is used; the legacy card-name string is ignored. |
| Merchant scope | Best Deal filters merchant-specific offers using the resolved merchant before running card eligibility. Without a matched merchant, only general offers are considered. |

The bank-or-product rule is the documented V1 contract in
`OfferEligibilityIntegrationTest`. The matcher has been implemented as a Spring
component to satisfy the existing service injection and rule.

## Amount calculation

All monetary calculations use `BigDecimal`, round to two decimal places with
`HALF_UP`, and follow these steps:

1. Calculate the discount as either purchase amount × percentage, or the flat
   discount value.
2. Apply `maxDiscount` when configured, then cap the discount at the purchase
   amount so checkout cannot become negative.
3. Subtract the discount to get the checkout amount.
4. Calculate cashback on the checkout amount when a cashback percentage is
   configured, then apply `maxCashback` when configured.
5. Calculate estimated effective price as checkout amount minus calculated
   cashback.
6. Calculate savings percentage from discount plus cashback divided by the
   original purchase amount.

Best Deal ranks by effective price, then checkout amount, then stable
alphabetical and ID tie-breakers. Cashback remains an estimate; LinkVault does
not model shipping, taxes, reward-point valuations, or successful cashback
fulfillment.

## Existing coverage and verification boundary

The repository contains integration cases for date validity, minimum-spend
boundaries, bank and card-product matches, OR semantics, discount caps,
purchase-amount caps, and cashback-based ranking. Those tests were inspected
but not run as part of this source audit.

Primary source files:

- `backend/src/main/java/com/linkvault/backend/offer/service/OfferEligibilityService.java`
- `backend/src/main/java/com/linkvault/backend/offer/service/OfferCalculationService.java`
- `backend/src/main/java/com/linkvault/backend/offer/applicability/service/OfferCardMatcher.java`
- `backend/src/main/java/com/linkvault/backend/deal/service/BestDealService.java`
- `backend/src/test/java/com/linkvault/backend/OfferEligibilityIntegrationTest.java`
- `backend/src/test/java/com/linkvault/backend/BestDealIntegrationTest.java`
