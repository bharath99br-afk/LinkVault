function formatAmount(value) {
    if (value === null || value === undefined || value === "") {
        return null;
    }

    const number = Number(value);

    if (Number.isNaN(number)) {
        return value;
    }

    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
    }).format(number);
}

function formatDiscount(offer) {
    const value = Number(offer.discountValue);

    if (Number.isNaN(value)) {
        return "Offer";
    }

    if (offer.discountType === "PERCENTAGE") {
        return `${value}% OFF`;
    }

    return `${formatAmount(value)} OFF`;
}

function getOfferStatus(offer) {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const startDate = new Date(`${offer.startDate}T00:00:00`);
    const endDate = new Date(`${offer.endDate}T00:00:00`);

    if (today < startDate) {
        return {
            label: "UPCOMING",
            className: "upcoming",
        };
    }

    if (today > endDate) {
        return {
            label: "EXPIRED",
            className: "expired",
        };
    }

    const millisecondsPerDay = 1000 * 60 * 60 * 24;

    const daysRemaining = Math.ceil(
        (endDate.getTime() - today.getTime()) /
        millisecondsPerDay
    );

    if (daysRemaining <= 3) {
        return {
            label:
                daysRemaining === 0
                    ? "ENDS TODAY"
                    : `ENDS IN ${daysRemaining} DAY${daysRemaining === 1 ? "" : "S"
                    }`,
            className: "ending",
        };
    }

    return {
        label: "ACTIVE",
        className: "active",
    };
}

function formatDate(date) {
    if (!date) {
        return "";
    }

    const parsedDate = new Date(`${date}T00:00:00`);

    if (Number.isNaN(parsedDate.getTime())) {
        return date;
    }

    return parsedDate.toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric",
    });
}

function OfferList({ offers }) {
    if (!offers.length) {
        return null;
    }

    return (
        <div className="offers-list">
            {offers.map((offer) => {
                const status = getOfferStatus(offer);

                return (
                    <article
                        key={offer.id}
                        className={`offer-card offer-card-${status.className}`}
                    >
                        <div className="offer-card-top">
                            <div className="offer-discount">
                                {formatDiscount(offer)}
                            </div>

                            <span
                                className={`offer-status offer-status-${status.className}`}
                            >
                                {status.label}
                            </span>
                        </div>

                        <div className="offer-card-content">
                            <div className="offer-card-heading">
                                <p className="offer-eyebrow">
                                    {offer.globalMerchantName
                                        ? "MERCHANT OFFER"
                                        : "GENERAL OFFER"}
                                </p>

                                <h2>{offer.title}</h2>
                            </div>

                            {offer.description && (
                                <p className="offer-description">
                                    {offer.description}
                                </p>
                            )}

                            <div className="offer-details">
                                {offer.globalMerchantName && (
                                    <div className="offer-detail">
                                        <span className="offer-detail-icon">
                                            ◉
                                        </span>

                                        <div>
                                            <span className="offer-detail-label">
                                                Merchant
                                            </span>

                                            <strong>
                                                {
                                                    offer.globalMerchantName
                                                }
                                            </strong>
                                        </div>
                                    </div>
                                )}

                                {!offer.globalMerchantName && (
                                    <div className="offer-detail">
                                        <span className="offer-detail-icon">
                                            ✦
                                        </span>

                                        <div>
                                            <span className="offer-detail-label">
                                                Availability
                                            </span>

                                            <strong>
                                                Available across eligible
                                                purchases
                                            </strong>
                                        </div>
                                    </div>
                                )}

                                {offer.minTransactionAmount && (
                                    <div className="offer-detail">
                                        <span className="offer-detail-icon">
                                            ₹
                                        </span>

                                        <div>
                                            <span className="offer-detail-label">
                                                Minimum spend
                                            </span>

                                            <strong>
                                                {formatAmount(
                                                    offer.minTransactionAmount
                                                )}
                                            </strong>
                                        </div>
                                    </div>
                                )}

                                {offer.maxDiscount && (
                                    <div className="offer-detail">
                                        <span className="offer-detail-icon">
                                            ↓
                                        </span>

                                        <div>
                                            <span className="offer-detail-label">
                                                Maximum saving
                                            </span>

                                            <strong>
                                                {formatAmount(
                                                    offer.maxDiscount
                                                )}
                                            </strong>
                                        </div>
                                    </div>
                                )}
                            </div>

                            <div className="offer-card-footer">
                                <div className="offer-validity">
                                    <span>Valid</span>

                                    <strong>
                                        {formatDate(offer.startDate)}
                                        {" — "}
                                        {formatDate(offer.endDate)}
                                    </strong>
                                </div>
                            </div>
                        </div>
                    </article>
                );
            })}
        </div>
    );
}

export default OfferList;