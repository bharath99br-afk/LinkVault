function formatAmount(value) {
    if (
        value === null ||
        value === undefined ||
        value === ""
    ) {
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

    const startDate = new Date(
        `${offer.startDate}T00:00:00`
    );

    const endDate = new Date(
        `${offer.endDate}T00:00:00`
    );

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

    const millisecondsPerDay =
        1000 * 60 * 60 * 24;

    const daysRemaining = Math.ceil(
        (endDate.getTime() - today.getTime()) /
        millisecondsPerDay
    );

    if (daysRemaining <= 3) {
        return {
            label:
                daysRemaining === 0
                    ? "ENDS TODAY"
                    : `ENDS IN ${daysRemaining} DAY${daysRemaining === 1
                        ? ""
                        : "S"
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

    const parsedDate = new Date(
        `${date}T00:00:00`
    );

    if (Number.isNaN(parsedDate.getTime())) {
        return date;
    }

    return parsedDate.toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric",
    });
}

function getOfferApplicabilityStatus(
    offerId,
    userCards,
    offerApplicability
) {
    const applicability =
        offerApplicability[offerId];

    if (!applicability) {
        return {
            status: "UNKNOWN",
            label: "",
            detail: "",
        };
    }

    const {
        banks = [],
        cards = [],
    } = applicability;

    if (cards.length > 0) {
        const matchedCard = cards
            .flatMap((offerCard) =>
                userCards.filter(
                    (userCard) =>
                        userCard.cardProductId !== null &&
                        Number(userCard.cardProductId) ===
                        Number(offerCard.cardProductId)
                )
            )[0];

        if (matchedCard) {
            return {
                status: "APPLICABLE",
                label: "Matches your card",
                detail:
                    matchedCard.cardProductName ||
                    matchedCard.name,
            };
        }

        return {
            status: "NOT_APPLICABLE",
            label: "Not applicable to your cards",
            detail: "Requires a different card",
        };
    }

    if (banks.length > 0) {
        const matchedCard = userCards.find(
            (userCard) =>
                banks.some(
                    (offerBank) =>
                        Number(userCard.bankId) ===
                        Number(offerBank.bankId)
                )
        );

        if (matchedCard) {
            return {
                status: "APPLICABLE",
                label: "Matches your bank",
                detail:
                    matchedCard.bankName ||
                    matchedCard.name,
            };
        }

        return {
            status: "NOT_APPLICABLE",
            label: "Not applicable to your cards",
            detail: "Requires a different bank",
        };
    }

    return {
        status: "GENERAL",
        label: "Available for your cards",
        detail: "No card restriction",
    };
}

function OfferList({
    offers,
    savedOfferIds,
    savingOfferIds,
    onSaveOffer,
    userCards,
    offerApplicability,
    applicabilityLoading,
}) {
    if (!offers.length) {
        return null;
    }

    return (
        <div className="offers-list">
            {offers.map((offer) => {
                const status = getOfferStatus(offer);
                const isGeneralOffer =
                    !offer.globalMerchantName;
                const isSaved = savedOfferIds.has(offer.id);
                const isSaving = savingOfferIds.has(offer.id);
                const isExpired = status.className === "expired";
                const applicabilityStatus =
                    getOfferApplicabilityStatus(
                        offer.id,
                        userCards,
                        offerApplicability
                    );

                return (
                    <article
                        key={offer.id}
                        className={`offer-card offer-card-${status.className}`}
                    >
                        <div className="offer-card-top">
                            <div>
                                <span className="offer-discovery-label">
                                    {isGeneralOffer
                                        ? "PAYMENT OFFER"
                                        : "MERCHANT DEAL"}
                                </span>

                                <div className="offer-discount">
                                    {formatDiscount(offer)}
                                </div>
                            </div>

                            <span
                                className={`offer-status offer-status-${status.className}`}
                            >
                                {status.label}
                            </span>
                        </div>

                        <div className="offer-card-content">
                            <div className="offer-card-heading">
                                <h2>{offer.title}</h2>

                                {offer.globalMerchantName ? (
                                    <p className="offer-merchant-name">
                                        {offer.globalMerchantName}
                                    </p>
                                ) : (
                                    <p className="offer-merchant-name">
                                        Available across eligible
                                        purchases
                                    </p>
                                )}
                            </div>

                            {offer.description && (
                                <p className="offer-description">
                                    {offer.description}
                                </p>
                            )}

                            {applicabilityLoading ? (
                                <div className="offer-applicability-status loading">
                                    <span className="offer-applicability-icon">
                                        ◌
                                    </span>

                                    <div>
                                        <strong>
                                            Checking your card eligibility
                                        </strong>

                                        <small>
                                            Matching this offer against your cards
                                        </small>
                                    </div>
                                </div>
                            ) : applicabilityStatus.status !== "UNKNOWN" ? (
                                <div
                                    className={`offer-applicability-status ${applicabilityStatus.status.toLowerCase()}`}
                                >
                                    <span className="offer-applicability-icon">
                                        {applicabilityStatus.status ===
                                            "APPLICABLE"
                                            ? "✓"
                                            : applicabilityStatus.status ===
                                                "NOT_APPLICABLE"
                                                ? "!"
                                                : "✦"}
                                    </span>

                                    <div className="offer-applicability-content">
                                        <strong>
                                            {applicabilityStatus.label}
                                        </strong>

                                        {applicabilityStatus.detail && (
                                            <small>
                                                {applicabilityStatus.detail}
                                            </small>
                                        )}
                                    </div>
                                </div>
                            ) : null}

                            <div className="offer-details">
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
                                    <span>
                                        Valid until
                                    </span>

                                    <strong>
                                        {formatDate(offer.endDate)}
                                    </strong>
                                </div>

                                <button
                                    type="button"
                                    className={`offer-save-discovery-button ${isSaved ? "saved" : ""
                                        }`}
                                    disabled={isSaved || isSaving || isExpired}
                                    onClick={() => onSaveOffer(offer.id)}
                                >
                                    {isSaving
                                        ? "Saving..."
                                        : isSaved
                                            ? "✓ Saved"
                                            : isExpired
                                                ? "Expired"
                                                : "Save Offer"}
                                </button>
                            </div>
                        </div>
                    </article>
                );
            })}
        </div>
    );
}

export default OfferList;