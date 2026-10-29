# Future Feature and Product Opportunity Tracker

Last updated: 29 October 2026

This working tracker brings the opportunity backlog from the LinkVault planning
folder into the application repository. It records current implementation
status and the next decision for each feature. “Implemented” describes code
found in the repository; it does not mean that the latest changes have passed
automated verification.

## Current development position

The roadmap's current checkpoint is Week 7, UX polish and MVP readiness, with
an MVP review and next-phase planning checkpoint dated 27 October. The purchase
context audit is complete. Existing integration tests cover offer validity,
minimum spend, card applicability, discount/cashback caps, merchant filtering,
and duplicate ingestion. Best Deal also covers saved Product URL merchant
matching and cross-user Product isolation. Ranking has a deterministic fallback
for financial ties, and no-result responses distinguish missing cards, no
active offers, and offers that do not qualify. Best Deal now shows how many
days remain before an eligible offer ends when its end date is known. Offer
eligibility checks active dates and minimum spend, then accepts a
matching bank or catalog card product when those restrictions are configured.
Discounts are capped at the offer limit and purchase amount; cashback is
estimated on the checkout amount and capped when configured. Desktop visual
review is complete for the Product list, saved-Product optimizer,
standalone Best Deal form, and recommendation alternatives. The form actions
align with their inputs, and the
calculation and tie explanation are readable without visible clipping or
overlap. The Best Deal promise is confirmed for the current MVP: compare offers
for one purchase at one store, rank by effective price after estimated
cashback, show checkout price separately, and disclose ties. The saved-Link
source flow has been reviewed and its prompts now refer to the saved page.
Mobile and saved-Link visual review remain before external data providers are
selected; AI remains deferred until the structured commerce data is reliable.

The form alignment issue is fixed in the shared Best Deal panel: the action
button now aligns with the merchant and amount controls instead of their helper
text. The optimizer is connected to both saved Products and legacy Links; it
can prefill a detected INR price, resolve a merchant, and disclose when only
general offers were considered. A recommendation from a saved item now links
back to its product page as the next step. MVP readiness findings are recorded
in [mvp-readiness-review.md](mvp-readiness-review.md); calculation rules are
recorded in [offer-calculation-audit.md](offer-calculation-audit.md). The
database migration approach is documented in
[database-migration-plan.md](database-migration-plan.md); external provider
selection remains deferred until the readiness review is closed.

## Product decision to resolve

| ID | Decision | Priority | Status / next step |
| --- | --- | --- | --- |
| PROD-01 | Confirm the Best Deal promise, purchase inputs, recommendation rule, and what the result does not claim | High | Confirmed for the current MVP: compare eligible offers for one purchase at one store, rank by effective price after estimated cashback, show checkout price separately, disclose ties, and do not compare product prices across stores. |
| UX-01 | Review the responsive Best Deal form after the shared alignment fix | Medium | Desktop visual review complete: saved-Product and standalone forms align with their controls. Mobile screenshot review remains; responsive CSS stacks the form at 540px and below. |

## Authentication and account

| ID | Feature | Priority | Status |
| --- | --- | --- | --- |
| AUTH-01 | Forgot password | High | Backlog |
| AUTH-02 | Password reset or change password | High | Backlog |
| AUTH-03 | Email verification | Medium | Backlog |
| AUTH-04 | Account profile and settings | Medium | Backlog |
| AUTH-05 | Session and device management | Later | Backlog |

## Links and saved products

| ID | Feature | Priority | Status / next step |
| --- | --- | --- | --- |
| LINK-01 | Better saved-product metadata | High | Partial. Product name, description, image, category, and merchant fields exist; current price is not persisted. |
| LINK-02 | Merchant auto-detection | High | Partial. The optimizer matches exact website hosts; regional domains, aliases, and seller identity are not covered. |
| LINK-03 | Product information extraction | High | Partial. Generic page metadata extraction exists; dedicated shopping-site providers remain a separate decision. |
| LINK-04 | Link health checking | Medium | Backlog |
| LINK-05 | Price-at-save tracking | Medium | Backlog |
| LINK-06 | Product price history | High | Backlog |
| LINK-07 | Price-drop notifications | High | Backlog |
| LINK-08 | Product categories and tags | Medium | Partial. A category field and filter exist; tags do not. |
| LINK-09 | Favorites | Medium | Backlog |
| LINK-10 | Collections and lists | Medium | Backlog |
| LINK-11 | Duplicate-link detection | Later | Backlog |
| LINK-12 | Bulk management | Later | Backlog |
| LINK-13 | Share a saved product | Later | Backlog |
| LINK-14 | Dedicated shopping-site provider support | High / mandatory | Planned after MVP review. Select an approved source and confirm terms, coverage, and reliability first. |

## Cards and payment optimization

| ID | Feature | Priority | Status / next step |
| --- | --- | --- | --- |
| CARD-01 | Card management improvements | High | Partial. Card CRUD exists; richer benefit and lifecycle management remains. |
| CARD-02 | Card expiry tracking | Medium | Backlog |
| CARD-03 | Card reward and benefit profile | High | Backlog |
| CARD-04 | Annual-fee awareness | Medium | Backlog |
| CARD-05 | Spending-category benefits | High | Backlog |
| CARD-06 | Card usage recommendations | High | Partial. Best Deal recommends a card for eligible configured offers; broader card benefits are not modeled. |
| CARD-07 | Monthly benefit and cap tracking | High | Backlog |

## Best Deal and purchase decisions

| ID | Feature | Priority | Status / next step |
| --- | --- | --- | --- |
| DEAL-01 | Savings history | High | Backlog; purchase decisions are not persisted as savings events. |
| DEAL-02 | Deal history | Medium | Backlog |
| DEAL-03 | Save a recommended deal | High | Backlog; saved offers are separate from saved purchase recommendations. |
| DEAL-04 | Deal-expiry countdown | High | Implemented in Best Deal recommendations and alternatives when the offer end date is known; no countdown is shown when the end date is missing. |
| DEAL-05 | Explain why an option is best | High | Implemented in the Best Deal result: explains the ranking rule and price calculation, compares the effective price with the next option, and discloses a financial tie. Desktop screenshot review complete; mobile review remains. |
| DEAL-06 | Compare alternative payment options | High | Implemented in the current UI; desktop alternatives visually reviewed, mobile review remains. |
| DEAL-07 | Minimum-spend optimization | High | Backlog |
| DEAL-08 | Stackable-offer detection | High | Backlog |
| DEAL-09 | Effective-price calculation | High | Implemented for configured discounts and cashback; other purchase costs and reward valuations are not modeled. |
| DEAL-10 | Purchase-timing recommendation | Medium | Backlog |
| DEAL-11 | Saved-product purchase context and offer-scope disclosure | High | In progress. Link/Product context, exact-host matching, general-only fallback, ownership checks, and distinct no-result reasons are covered. The saved-Product desktop flow is visually reviewed; the saved-Link source flow is reviewed and its prompts use neutral saved-page wording. Saved-Link and mobile visual review remain. |
| DEAL-12 | Continue from a recommendation to the saved page | High | Implemented for valid saved URLs; the user confirms price and terms and completes checkout at the store. |
| DEAL-13 | Explain why no recommendation was returned | High | Implemented: distinguishes missing cards, no active offers in scope, and offers that do not qualify; links to My Cards or Offers where useful. |

## Engagement and retention

| ID | Feature | Priority | Status |
| --- | --- | --- | --- |
| RET-01 | Price-drop alerts | High | Backlog |
| RET-02 | Offer-expiry alerts | High | Backlog |
| RET-03 | New-card-offer alerts | High | Backlog |
| RET-04 | Personalized savings notifications | High | Backlog |
| RET-05 | Monthly savings report | High | Backlog |
| RET-06 | “You could have saved” insights | Medium | Backlog |
| RET-07 | Personalized deal feed | Medium | Backlog |
| RET-08 | Wishlist monitoring | High | Backlog |
| RET-09 | Weekly deal digest | Medium | Backlog |
| RET-10 | Savings milestones and achievements | Later | Backlog |

## Real-world data and provenance

| ID | Feature | Priority | Status / next step |
| --- | --- | --- | --- |
| DATA-01 | Select approved product and offer data providers | High | Planned after MVP validation; review availability, licensing, terms, rate limits, and commercial fit. |
| DATA-02 | Track offer source, verification time, and refresh status | High | Planned for the real-world data milestone, before external data enters the core offer engine. |

## Monetization

| ID | Opportunity | Potential | Status |
| --- | --- | --- | --- |
| MON-01 | Affiliate commissions | High | Deferred until the product earns user trust and source links can be attributed reliably. |
| MON-02 | Premium purchase optimization | High | Deferred |
| MON-03 | Advanced price tracking | High | Deferred |
| MON-04 | Premium alerts | Medium | Deferred |
| MON-05 | Advanced analytics | Medium | Deferred |
| MON-06 | Personalized financial and product recommendations | Medium | Deferred |
| MON-07 | Merchant partnerships | High | Deferred |
| MON-08 | Sponsored deals with clear labeling | Later | Deferred |

## Next checkpoints

1. Complete the saved-Link and mobile visual review, including price
   confirmation, merchant matching limits, no-match behavior, and the route
   back to the saved page.
2. Capture and verify the existing database schema, reconcile the manual SQL
   fragments, and build a baseline using
   [database-migration-plan.md](database-migration-plan.md).
3. Close the remaining readiness actions in
   [mvp-readiness-review.md](mvp-readiness-review.md) before selecting a
   provider for LINK-14 and DATA-01.
4. Keep AI and monetization deferred until the deterministic purchase flow
   and structured data are reliable.
