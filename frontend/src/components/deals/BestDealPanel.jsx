import { useState } from "react";

import { findBestDeal } from "../../services/dealService";

function BestDealPanel({ linkId = null, productTitle = "", onClose }) {
  const [transactionAmount, setTransactionAmount] = useState("");

  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    const normalizedAmount = transactionAmount.trim();

    if (!normalizedAmount) {
      setError("Enter a purchase amount.");
      setResult(null);
      return;
    }

    const amount = Number(normalizedAmount);

    if (!Number.isFinite(amount) || amount <= 0) {
      setError("Purchase amount must be greater than 0.");
      setResult(null);
      return;
    }

    setLoading(true);
    setError("");
    setResult(null);

    try {
      const response = await findBestDeal({
        linkId,
        transactionAmount: amount,
      });

      setResult(response?.data || null);
    } catch (requestError) {
      console.error("Best deal request failed:", requestError);

      setError(requestError.data?.message || "Failed to find the best deal.");
    } finally {
      setLoading(false);
    }
  };

  const bestDeal = result?.bestDeal;
  const alternatives = result?.alternatives || [];

  return (
    <section className="best-deal-panel">
      <div className="best-deal-header-row">
        <div className="best-deal-header">
          <p className="best-deal-eyebrow">SAVE MORE</p>

          <h2>Find your best deal</h2>

          <p>
            {productTitle
              ? `See how you can pay less for ${productTitle}.`
              : "Enter what you're about to spend and LinkVault will compare your eligible offers and cards."}
          </p>
        </div>

        <button
          type="button"
          className="best-deal-close"
          onClick={onClose}
          aria-label="Close best deal"
        >
          ×
        </button>
      </div>

      <form className="best-deal-form" onSubmit={handleSubmit} noValidate>
        <div className="best-deal-input-group">
          <label htmlFor="transactionAmount">Purchase amount</label>

          <div className="best-deal-input-wrapper">
            <span aria-hidden="true">₹</span>

            <input
              id="transactionAmount"
              type="number"
              inputMode="decimal"
              step="0.01"
              value={transactionAmount}
              onChange={(event) => setTransactionAmount(event.target.value)}
              placeholder="10,000"
              disabled={loading}
            />
          </div>
        </div>

        <button type="submit" className="best-deal-submit" disabled={loading}>
          {loading ? "Finding deal..." : "Find Best Deal"}
        </button>
      </form>

      {error && (
        <div className="best-deal-error" role="alert">
          {error}
        </div>
      )}

      {!loading && result && !bestDeal && (
        <div className="best-deal-empty">
          <h3>No eligible deals found</h3>

          <p>
            We couldn't find an active offer that applies to your cards for this
            purchase.
          </p>
        </div>
      )}

      {!loading && bestDeal && (
        <div className="best-deal-results">
          <div className="best-deal-result-card">
            <div className="best-deal-result-badge">BEST DEAL</div>

            <h3>{bestDeal.offerTitle}</h3>

            <p className="best-deal-card-name">Use {bestDeal.cardName}</p>

            <div className="best-deal-amounts">
              <div>
                <span>Purchase</span>
                <strong>
                  ₹
                  {Number(bestDeal.transactionAmount).toLocaleString("en-IN", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </strong>
              </div>

              <div>
                <span>You save</span>
                <strong>
                  ₹
                  {Number(bestDeal.discountAmount).toLocaleString("en-IN", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </strong>
              </div>

              <div>
                <span>Effective price</span>
                <strong>
                  ₹
                  {Number(bestDeal.finalAmount).toLocaleString("en-IN", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </strong>
              </div>
            </div>
          </div>

          {alternatives.length > 0 && (
            <div className="best-deal-alternatives">
              <div className="best-deal-alternatives-header">
                <h3>Other eligible options</h3>

                <span>{alternatives.length}</span>
              </div>

              <div className="best-deal-alternatives-list">
                {alternatives.map((deal, index) => (
                  <article
                    key={`${deal.offerId}-${deal.cardId}-${index}`}
                    className="best-deal-alternative"
                  >
                    <div>
                      <h4>{deal.offerTitle}</h4>

                      <p>Use {deal.cardName}</p>
                    </div>

                    <div className="best-deal-alternative-price">
                      <span>
                        Save ₹
                        {Number(deal.discountAmount).toLocaleString("en-IN", {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        })}
                      </span>

                      <strong>
                        ₹
                        {Number(deal.finalAmount).toLocaleString("en-IN", {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        })}
                      </strong>
                    </div>
                  </article>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </section>
  );
}

export default BestDealPanel;
