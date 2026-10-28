import { useEffect, useRef, useState } from "react";
import { getMerchants } from "../../services/merchantService";
import { findBestDeal } from "../../services/dealService";
import { previewProduct } from "../../services/productService";

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
  const hasSavedProductContext = linkId != null || productId != null;

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

    if (!hasSavedProductContext || !productUrl) {
      return undefined;
    }

    let current = true;
    setPriceHint("Checking the saved product page for a listed INR price…");

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
  }, [linkId, productId, productUrl, hasSavedProductContext]);

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
              ? `Compare payment options for ${productTitle}.`
              : "Choose a store and purchase amount to compare eligible offers across your cards."}
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
              {hasSavedProductContext
                ? "Auto-detect from saved product"
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
              general offers and try to identify the saved product’s store.
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
              (hasSavedProductContext
                ? "Enter the current store price. We’ll compare eligible offers for this product."
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
              : hasSavedProductContext
                ? "The store could not be identified for this saved product. Showing general offers only; select one of your saved stores to include store-specific offers."
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
          <h3>No eligible deals found</h3>
          <p>
            We couldn’t find an eligible offer for this purchase and your
            available cards. Try another amount or check your saved cards and
            offers.
          </p>
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

            {renderCalculation(bestDeal)}
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
