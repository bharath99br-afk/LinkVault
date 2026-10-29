import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { getMerchants } from "../../services/merchantService";
import { findBestDeal } from "../../services/dealService";
import { previewProduct } from "../../services/productService";
import { validateProductUrl } from "../../utils/urlUtils";

const formatINR = (value) => {
  const amount = Number(value);

  if (value === null || value === undefined || !Number.isFinite(amount)) {
    return "—";
  }

  return amount.toLocaleString("en-IN", {
    style: "currency",
    currency: "INR",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

const getOfferExpiryMessage = (daysUntilExpiry) => {
  if (daysUntilExpiry === null || daysUntilExpiry === undefined) {
    return null;
  }

  const days = Number(daysUntilExpiry);

  if (!Number.isInteger(days) || days < 0) {
    return null;
  }

  if (days === 0) {
    return "Offer ends today";
  }

  if (days === 1) {
    return "Offer ends tomorrow";
  }

  return `Offer ends in ${days} days`;
};

const getRecommendationExplanation = (deal, nextDeal) => {
  const toPaise = (value) => {
    const amount = Number(value);
    return Number.isFinite(amount) ? Math.round(amount * 100) : null;
  };

  const purchaseAmount = toPaise(deal.transactionAmount);
  const discountAmount = toPaise(deal.discountAmount ?? 0);
  const cashbackAmount = toPaise(deal.cashbackAmount ?? 0);
  const effectivePrice = toPaise(deal.effectivePrice);
  const checkoutAmount = toPaise(deal.finalAmount);

  let comparison = "";

  if (nextDeal) {
    const nextEffectivePrice = toPaise(nextDeal.effectivePrice);
    const nextCheckoutAmount = toPaise(nextDeal.finalAmount);

    if (
      effectivePrice !== null &&
      checkoutAmount !== null &&
      nextEffectivePrice !== null &&
      nextCheckoutAmount !== null
    ) {
      if (effectivePrice < nextEffectivePrice) {
        comparison = `That is ${formatINR((nextEffectivePrice - effectivePrice) / 100)} lower in estimated effective price than the next eligible option.`;
      } else if (
        effectivePrice === nextEffectivePrice &&
        checkoutAmount < nextCheckoutAmount
      ) {
        comparison = `It ties the next option on effective price and requires ${formatINR((nextCheckoutAmount - checkoutAmount) / 100)} less at checkout.`;
      } else if (
        effectivePrice === nextEffectivePrice &&
        checkoutAmount === nextCheckoutAmount
      ) {
        comparison =
          "It ties the next option on both effective price and checkout amount; LinkVault’s current calculation finds no financial difference between them.";
      }
    }
  }

  const calculation =
    purchaseAmount !== null &&
    discountAmount !== null &&
    cashbackAmount !== null &&
    effectivePrice !== null
      ? `${formatINR(purchaseAmount / 100)} purchase − ${formatINR(discountAmount / 100)} instant discount − ${formatINR(cashbackAmount / 100)} estimated cashback = ${formatINR(effectivePrice / 100)} estimated effective price.`
      : "The effective price is calculated from the eligible offer’s discount and estimated cashback.";

  return { calculation, comparison };
};

const getSafeProductUrl = (value) => {
  if (!value) {
    return "";
  }

  const validation = validateProductUrl(String(value));

  if (!validation.valid) {
    return "";
  }

  try {
    const url = new URL(validation.value);

    if (url.username || url.password) {
      return "";
    }

    return url.toString();
  } catch {
    return "";
  }
};

const getNoDealState = (reason, offerScope, merchantName) => {
  if (reason === "NO_SAVED_CARDS") {
    return {
      title: "Add a card to compare payment options",
      message:
        "LinkVault checks eligible offers against the cards you have saved. Add a card to see payment options for this purchase.",
      action: { label: "Go to My Cards", to: "/cards" },
    };
  }

  if (reason === "NO_ACTIVE_OFFERS") {
    if (offerScope === "GENERAL_ONLY") {
      if (merchantName) {
        return {
          title: "Only general offers could be checked",
          message: `LinkVault couldn’t match ${merchantName} to store-specific offers. Select a saved store with matching offers, or check again when a general offer is active.`,
        };
      }

      return {
        title: "No active general offers found",
        message:
          "Select a saved store above to include store-specific offers, or check back when a general offer is available.",
      };
    }

    return {
      title: "No active offers found for this store",
      message: merchantName
        ? `We checked ${merchantName} and general offers, but none are active right now.`
        : "We checked store-specific and general offers, but none are active right now.",
      action: { label: "Browse offers", to: "/offers" },
    };
  }

  return {
    title: "No eligible deals found",
    message:
      "Active offers were found, but none matched this purchase amount and your saved cards. Review the amount, card details, and offer terms.",
  };
};

function BestDealPanel({
  linkId = null,
  productId = null,
  productTitle = "",
  productUrl = "",
  onClose,
}) {
  const [transactionAmount, setTransactionAmount] = useState("");
  const [merchantId, setMerchantId] = useState("");
  const [merchants, setMerchants] = useState([]);
  const [merchantsLoading, setMerchantsLoading] = useState(true);
  const [merchantLoadError, setMerchantLoadError] = useState(false);
  const [priceHint, setPriceHint] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const requestVersion = useRef(0);
  const hasSavedItemContext = linkId != null || productId != null;

  useEffect(() => {
    let current = true;

    getMerchants({ page: 0, size: 100 })
      .then((response) => {
        if (current) {
          setMerchants(response?.data?.content || []);
        }
      })
      .catch(() => {
        if (current) {
          setMerchants([]);
          setMerchantLoadError(true);
        }
      })
      .finally(() => {
        if (current) {
          setMerchantsLoading(false);
        }
      });

    return () => {
      current = false;
    };
  }, []);

  useEffect(() => {
    requestVersion.current += 1;
    setLoading(false);
    setTransactionAmount("");
    setMerchantId("");
    setResult(null);
    setError("");
    setPriceHint("");

    if (!hasSavedItemContext || !productUrl) {
      return undefined;
    }

    let current = true;
    setPriceHint("Checking the saved page for a listed INR price…");

    previewProduct(productUrl)
      .then((response) => {
        if (!current) {
          return;
        }

        const preview = response?.data;
        const listedPrice = Number(preview?.price);
        const currency = String(preview?.currency || "")
          .trim()
          .toUpperCase();

        if (currency === "INR" && Number.isFinite(listedPrice) && listedPrice > 0) {
          setTransactionAmount((amount) =>
            amount.trim() ? amount : String(listedPrice),
          );
          setPriceHint(
            `Listed price found: ${formatINR(listedPrice)}. Confirm it on the store page; you can edit it before comparing.`,
          );
          return;
        }

        if (preview?.price != null) {
          setPriceHint(
            currency
              ? `The page lists ${currency} ${preview.price}. Enter the INR amount you expect to pay; LinkVault does not convert currencies.`
              : "A listed price was found, but its currency is unclear. Enter the INR amount you expect to pay.",
          );
          return;
        }

        setPriceHint(
          "No listed price was found on this page. Enter the current INR price from the store.",
        );
      })
      .catch(() => {
        if (current) {
          setPriceHint(
            "We couldn't read a price from this page. Enter the current INR price from the store.",
          );
        }
      });

    return () => {
      current = false;
    };
  }, [linkId, productId, productUrl, hasSavedItemContext]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const normalizedAmount = transactionAmount.trim();
    const amount = Number(normalizedAmount);

    if (!normalizedAmount || !Number.isFinite(amount) || amount <= 0) {
      setError(
        !normalizedAmount
          ? "Enter a purchase amount."
          : "Purchase amount must be greater than 0.",
      );
      setResult(null);
      return;
    }

    const version = requestVersion.current + 1;
    requestVersion.current = version;
    setLoading(true);
    setError("");
    setResult(null);

    try {
      const response = await findBestDeal({
        linkId,
        productId,
        merchantId: merchantId ? Number(merchantId) : null,
        transactionAmount: amount,
      });

      if (requestVersion.current === version) {
        setResult(response?.data || null);
      }
    } catch (requestError) {
      console.error("Best deal request failed:", requestError);
      if (requestVersion.current === version) {
        setError(requestError.data?.message || "Failed to find the best deal.");
      }
    } finally {
      if (requestVersion.current === version) {
        setLoading(false);
      }
    }
  };

  const bestDeal = result?.bestDeal;
  const alternatives = result?.alternatives || [];
  const safeProductUrl = getSafeProductUrl(productUrl);
  const recommendationExplanation = bestDeal
    ? getRecommendationExplanation(bestDeal, alternatives[0])
    : null;
  const noDealState = result
    ? getNoDealState(
        result.noDealReason,
        result.offerScope,
        result.merchantName,
      )
    : null;

  const renderCalculation = (deal) => (
    <div className="best-deal-calculation">
      <div className="best-deal-calculation-row">
        <span>Original purchase amount</span>
        <strong>{formatINR(deal.transactionAmount)}</strong>
      </div>

      <div className="best-deal-calculation-row">
        <span>Instant discount</span>
        <strong className="best-deal-saving">
          −{formatINR(deal.discountAmount)}
        </strong>
      </div>

      <div className="best-deal-calculation-row best-deal-checkout-row">
        <span>Amount to pay at checkout</span>
        <strong>{formatINR(deal.finalAmount)}</strong>
      </div>

      <div className="best-deal-calculation-row">
        <span>Estimated cashback</span>
        <strong className="best-deal-saving">
          −{formatINR(deal.cashbackAmount ?? 0)}
        </strong>
      </div>

      <div className="best-deal-calculation-row best-deal-effective-row">
        <span>Effective price after cashback</span>
        <strong>{formatINR(deal.effectivePrice)}</strong>
      </div>

      <p className="best-deal-disclaimer">
        Cashback is an estimate. Your actual benefit depends on the offer terms
        and successful fulfilment.
      </p>
    </div>
  );

  return (
    <section className="best-deal-panel" aria-labelledby="best-deal-heading">
      <div className="best-deal-header-row">
        <div className="best-deal-header">
          <p className="best-deal-eyebrow">PURCHASE OPTIMIZER</p>
          <h2 id="best-deal-heading">Find your best deal</h2>
          <p>
            {productTitle
              ? `Compare eligible offers across your saved cards for ${productTitle} at the amount you enter. LinkVault does not compare product prices across stores.`
              : "Choose a store and purchase amount to compare eligible offers across your cards. LinkVault compares payment options, not product prices across stores."}
          </p>
        </div>

        {onClose && (
          <button
            type="button"
            className="best-deal-close"
            onClick={onClose}
            aria-label="Close best deal"
          >
            ×
          </button>
        )}
      </div>

      <form className="best-deal-form" onSubmit={handleSubmit} noValidate>
        <div className="best-deal-input-group">
          <label htmlFor="dealMerchant">Store (optional)</label>
          <select
            id="dealMerchant"
            className="best-deal-merchant-select"
            value={merchantId}
            onChange={(event) => {
              setMerchantId(event.target.value);
              setResult(null);
              setError("");
            }}
            disabled={merchantsLoading || loading}
            aria-describedby="best-deal-merchant-hint"
          >
            <option value="">
              {hasSavedItemContext
                ? "Auto-detect from saved page"
                : "No store selected"}
            </option>
            {merchants.map((merchant) => (
              <option key={merchant.id} value={merchant.id}>
                {merchant.name}
              </option>
            ))}
          </select>
          <p id="best-deal-merchant-hint" className="best-deal-input-hint">
            Select a saved store to include its offers. Without a matched store,
            only general offers are checked.
          </p>
          {merchantLoadError && (
            <p className="best-deal-input-hint" role="status">
              Your saved stores could not be loaded. LinkVault will still check
              general offers and try to identify the saved page’s store.
            </p>
          )}
        </div>

        <div className="best-deal-input-group">
          <label htmlFor="transactionAmount">Purchase amount (₹)</label>
          <div className="best-deal-input-wrapper">
            <span aria-hidden="true">₹</span>
            <input
              id="transactionAmount"
              type="number"
              inputMode="decimal"
              min="0.01"
              step="0.01"
              value={transactionAmount}
              onChange={(event) => {
                setTransactionAmount(event.target.value);
                setResult(null);
                setError("");
              }}
              placeholder="10,000"
              disabled={loading}
              aria-describedby="best-deal-input-hint"
              required
            />
          </div>
          <p id="best-deal-input-hint" className="best-deal-input-hint">
            {priceHint ||
              (hasSavedItemContext
                ? "Enter the current store price. We’ll compare eligible offers for this purchase."
                : "Enter the amount you expect to pay. An amount alone only qualifies for general offers.")}
          </p>
        </div>

        <button type="submit" className="best-deal-submit" disabled={loading}>
          {loading ? "Comparing options…" : "Find best deal"}
        </button>
      </form>

      {result && (
        <p className="best-deal-context-note" role="status">
          {result.offerScope === "MERCHANT_AND_GENERAL" && result.merchantName
            ? `Checked ${result.merchantName} offers and general offers.`
            : result.merchantName
              ? `Could not match ${result.merchantName} to store offers. Showing general offers only.`
              : hasSavedItemContext
                ? "The store could not be identified from this saved page. Showing general offers only; select one of your saved stores to include store-specific offers."
                : "No store was matched. Showing general offers only; select a saved store to include store-specific offers."}
        </p>
      )}

      {loading && (
        <div className="best-deal-loading" role="status" aria-live="polite">
          <span className="best-deal-loading-indicator" aria-hidden="true" />
          <span>
            Checking eligible offers and calculating effective prices…
          </span>
        </div>
      )}

      {error && (
        <div className="best-deal-error" role="alert">
          <strong>We couldn’t calculate your best deal.</strong>
          <p>{error}</p>
        </div>
      )}

      {!loading && result && !bestDeal && (
        <div className="best-deal-empty" role="status">
          <h3>{noDealState.title}</h3>
          <p>{noDealState.message}</p>
          {noDealState.action && (
            <Link
              className="best-deal-empty-action"
              to={noDealState.action.to}
            >
              {noDealState.action.label}
            </Link>
          )}
        </div>
      )}

      {!loading && bestDeal && (
        <div className="best-deal-results" aria-live="polite">
          <section className="best-deal-result-card">
            <div className="best-deal-result-topline">
              <span className="best-deal-result-badge">BEST MATCH</span>
              <span className="best-deal-result-caption">
                Lowest effective price
              </span>
            </div>

            <h3>{bestDeal.offerTitle || "Recommended offer"}</h3>
            {getOfferExpiryMessage(bestDeal.daysUntilExpiry) && (
              <p className="best-deal-expiry">
                {getOfferExpiryMessage(bestDeal.daysUntilExpiry)}
              </p>
            )}
            <p className="best-deal-card-name">
              Pay with <strong>{bestDeal.cardName || "Selected card"}</strong>
              {bestDeal.bankName ? ` · ${bestDeal.bankName}` : ""}
            </p>

            <div className="best-deal-price-summary">
              <div className="best-deal-effective-price">
                <span>Effective price</span>
                <strong>{formatINR(bestDeal.effectivePrice)}</strong>
                <small>After estimated cashback</small>
              </div>

              <div className="best-deal-checkout-price">
                <span>Pay at checkout</span>
                <strong>{formatINR(bestDeal.finalAmount)}</strong>
              </div>
            </div>

            <section
              className="best-deal-explanation"
              aria-labelledby="best-deal-explanation-heading"
            >
              <h4 id="best-deal-explanation-heading">
                Why this is recommended
              </h4>
              <p>
                Ranked by the lowest estimated effective price among eligible
                card and offer combinations.
              </p>
              <p className="best-deal-explanation-calculation">
                {recommendationExplanation.calculation}
              </p>
              {recommendationExplanation.comparison && (
                <p className="best-deal-explanation-comparison">
                  {recommendationExplanation.comparison}
                </p>
              )}
            </section>

            {renderCalculation(bestDeal)}

            {safeProductUrl && (
              <div className="best-deal-next-step">
                <a
                  className="best-deal-store-link"
                  href={safeProductUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Open saved page
                </a>
                <p>
                  Confirm the current price and offer terms on the store page
                  before paying. LinkVault does not place orders or apply offers.
                </p>
              </div>
            )}
          </section>

          {alternatives.length > 0 && (
            <section className="best-deal-alternatives">
              <div className="best-deal-alternatives-header">
                <div>
                  <h3>Compare alternatives</h3>
                  <p>Other eligible options, ranked by effective price.</p>
                </div>
                <span aria-label={`${alternatives.length} alternatives`}>
                  {alternatives.length}
                </span>
              </div>

              <div className="best-deal-alternatives-list">
                {alternatives.map((deal, index) => (
                  <article
                    key={`${deal.offerId}-${deal.cardId}-${index}`}
                    className="best-deal-alternative"
                  >
                    <div className="best-deal-alternative-details">
                      <span className="best-deal-rank">OPTION {index + 2}</span>
                      <h4>{deal.offerTitle || "Eligible offer"}</h4>
                      {getOfferExpiryMessage(deal.daysUntilExpiry) && (
                        <p className="best-deal-expiry">
                          {getOfferExpiryMessage(deal.daysUntilExpiry)}
                        </p>
                      )}
                      <p>
                        {deal.cardName || "Selected card"}
                        {deal.bankName ? ` · ${deal.bankName}` : ""}
                      </p>
                    </div>

                    <div className="best-deal-alternative-prices">
                      <div>
                        <span>Effective price</span>
                        <strong>{formatINR(deal.effectivePrice)}</strong>
                      </div>
                      <div>
                        <span>Pay at checkout</span>
                        <strong>{formatINR(deal.finalAmount)}</strong>
                      </div>
                      <div>
                        <span>Est. cashback</span>
                        <strong>{formatINR(deal.cashbackAmount ?? 0)}</strong>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          )}
        </div>
      )}
    </section>
  );
}

export default BestDealPanel;
