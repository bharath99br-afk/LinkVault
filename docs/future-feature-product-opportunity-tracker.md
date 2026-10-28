# Future Feature and Product Opportunity Tracker

Last updated: 28 October 2026

This working tracker brings the opportunity backlog from the LinkVault planning
folder into the application repository. It records current implementation
status and the next decision for each feature. “Implemented” describes code
found in the repository; it does not mean that the latest changes have passed
automated verification.

## Current development position

The roadmap's current checkpoint is Week 7, UX polish and MVP readiness, with
an MVP review and next-phase planning checkpoint dated 27 October. The Best
Deal experience still needs product review. Keep the next work focused on
that purchase decision and its trust signals before starting external data
providers. The roadmap places real-world data after MVP validation and AI
after reliable structured commerce data.

The form alignment issue is fixed in the shared Best Deal panel: the action
button now aligns with the merchant and amount controls instead of their helper
text. The optimizer is connected to both saved Products and legacy Links; it
can prefill a detected INR price, resolve a merchant, and disclose when only
general offers were considered.

## Product decision to resolve

| ID | Decision | Priority | Status / next step |
| --- | --- | --- | --- |
| PROD-01 | Confirm the Best Deal promise, purchase inputs, recommendation rule, and what the result does not claim | High | Needs product review. The current implementation compares payment options for one product and merchant; it does not find the lowest product price across stores. |
| UX-01 | Review the responsive Best Deal form after the shared alignment fix | Medium | Implemented in CSS; visual review remains. |

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
| DEAL-04 | Deal-expiry countdown | High | Backlog |
| DEAL-05 | Explain why an option is best | High | Implemented in the Best Deal result: explains the ranking rule and price calculation, compares the effective price with the next option, and discloses an exact tie. Visual review remains. |
| DEAL-06 | Compare alternative payment options | High | Implemented in the current UI; QA remains. |
| DEAL-07 | Minimum-spend optimization | High | Backlog |
| DEAL-08 | Stackable-offer detection | High | Backlog |
| DEAL-09 | Effective-price calculation | High | Implemented for configured discounts and cashback; other purchase costs and reward valuations are not modeled. |
| DEAL-10 | Purchase-timing recommendation | Medium | Backlog |
| DEAL-11 | Saved-product purchase context and offer-scope disclosure | High | In progress. Link/Product context, merchant matching, and general-only fallback exist; confirm the experience and verify behavior before treating the MVP flow as complete. |

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

1. Review PROD-01 and DEAL-05: agree what “best” means and what evidence the
   recommendation must show.
2. Review the saved Product and Link flow, including price confirmation,
   merchant matching limits, no-match behavior, and the route back to the
   product page.
3. Complete MVP readiness review before selecting a provider for LINK-14 and
   DATA-01.
4. Keep AI and monetization deferred until the deterministic purchase flow
   and structured data are reliable.
