# Purchase-first optimizer

## Product goal

Help someone who is about to buy a saved product choose the best eligible card
and offer for that purchase. LinkVault compares payment outcomes for one
product and one merchant; it does not currently compare the product's price
across stores.

## MVP business rules

1. The purchase amount is the price the user expects to pay at the selected
   store. When a saved product page exposes a price in INR, LinkVault may
   prefill it. The user can edit it and must submit the comparison. If the page
   has no price, has an unknown currency, or is not reachable, the user enters
   the INR amount manually. LinkVault does not convert currencies.
2. Resolve the merchant from an explicit saved-merchant selection first. If
   there is no selection, use a saved Link merchant, then the associated
   Product merchant, then an exact host match against the Link URL and Product
   URL. `www.` is ignored; other subdomains and regional domains are not
   treated as aliases.
3. Include active general offers and offers for the resolved merchant. Exclude
   offers for other merchants. If no merchant can be matched, compare general
   offers only; do not present another store's promotion as eligible.
4. Evaluate only cards owned by the signed-in user. The existing eligibility
   and calculation services enforce offer dates, minimum transaction amount,
   bank/card-product applicability, discount caps, and configured cashback
   caps.
5. Rank by calculated effective price (checkout amount less configured
   cashback), then by checkout amount. When both figures tie, order options
   alphabetically by offer title and card name, using their IDs as a final
   stable fallback. Show checkout amount and estimated cashback separately so
   a delayed benefit is not presented as an instant discount. Explain the
   ranking with the purchase amount, instant discount, estimated cashback, and
   effective price; compare the result with the next option when the figures
   establish a financial advantage, and disclose a financial tie.
6. Return no recommendation when no eligible offer/card combination exists,
   and distinguish missing cards, no active offers for the selected scope, and
   active offers that do not qualify for the purchase.
7. When the comparison starts from a saved Link or Product, provide a safe link
   back to that saved page after a recommendation. The user confirms the current
   price and applies the offer at the store; LinkVault does not place orders.

## Current data flow

```text
Saved Product or Link
  -> current page metadata price (best effort) or user-entered amount
  -> selected / associated / URL-matched merchant
  -> active general + matching merchant offers
  -> signed-in user's cards
  -> eligibility and calculation services
  -> ranked recommendation and alternatives
```

The frontend has two saved-item models. The legacy Links flow stores a title
and URL and can optionally reference a Merchant or Product, but its current
form sends only the title and URL. The Products flow stores product metadata
and an optional Merchant. Neither model persists a current price. Product page
preview can extract a price and currency from page metadata, but the deal
comparison now reads that metadata when the optimizer opens and only prefills
an INR price.

The optimizer is available from both saved-item pages and the standalone Best
Deal route. The request can identify either a Link or a Product, plus an
optional user-owned Merchant. Link and Product IDs are ownership-checked; a
selected Merchant is also checked against the signed-in user.

## Project map

- `frontend/src/pages/Links.jsx` and `frontend/src/pages/Products.jsx` provide
  the saved-item entry points.
- `frontend/src/components/deals/BestDealPanel.jsx` collects or prefills the
  amount and merchant context, explains the returned recommendation, and links
  back to the saved page when one was supplied.
- `frontend/src/services/dealService.js` sends the purchase context to the API.
- `backend/src/main/java/com/linkvault/backend/deal/service/BestDealService.java`
  resolves purchase context, scopes offers, evaluates the user's cards, and
  ranks results.
- `backend/src/main/java/com/linkvault/backend/offer/service/OfferEligibilityService.java`
  and `OfferCalculationService.java` own eligibility and amount calculations.
- `backend/src/main/java/com/linkvault/backend/product/service/ProductPreviewService.java`
  and `product/metadata/` fetch product page metadata. The HTTP fetcher blocks
  redirects and private/local addresses.
- Other backend areas cover authentication/security, cards and card catalog,
  banks, merchants, offers/applicability, saved offers, and dashboard summaries.

## Known limits and next decisions

- Page metadata is best effort and may be stale, missing, or wrong; the user
  should confirm the amount on the store page before comparing.
- Merchant matching is exact-host matching, not alias or marketplace-seller
  resolution. Regional domains and alternate store hosts need explicit alias
  data or a user-selected Merchant that maps to a GlobalMerchant.
- Cashback is treated as value in the MVP ranking. The model does not yet
  represent payout delay, redemption restrictions, exclusions, or certainty.
- Offers are evaluated individually. Stacking, taxes, shipping, card fees,
  reward-point valuation, and checkout verification are outside the current
  calculation model.
- Opening the saved product page is a handoff only. LinkVault does not place an
  order, apply a discount, or verify the final checkout amount.
- Equal effective price and checkout amount are not a financial win for either
  option. The backend returns them in a stable alphabetical order, and the UI
  tells the user when the next option ties on both figures.
- Links and Products remain separate saved-item models. The optimizer supports
  both, but consolidation and migration of legacy Links need a separate
  product decision.
