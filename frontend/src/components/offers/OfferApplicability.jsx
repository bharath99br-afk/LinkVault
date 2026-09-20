import { useEffect, useState } from "react";

import { getCardProducts } from "../../services/cardProductService";

function OfferApplicability({
    banks,
    banksLoading,
    mode,
    selectedBankIds,
    selectedCardProductIds,
    onModeChange,
    onBankSelectionChange,
    onCardProductSelectionChange,
}) {
    const [selectedBankForCards, setSelectedBankForCards] =
        useState("");

    const [cardProducts, setCardProducts] = useState([]);
    const [cardProductsLoading, setCardProductsLoading] =
        useState(false);

    const [cardProductsError, setCardProductsError] =
        useState("");

    useEffect(() => {
        setSelectedBankForCards("");
        setCardProducts([]);
        setCardProductsError("");
    }, [mode]);

    useEffect(() => {
        if (!selectedBankForCards) {
            setCardProducts([]);
            return;
        }

        const loadCardProducts = async () => {
            setCardProductsLoading(true);
            setCardProductsError("");

            try {
                const response = await getCardProducts({
                    bankId: selectedBankForCards,
                    page: 0,
                    size: 100,
                });

                setCardProducts(
                    response?.data?.content || []
                );
            } catch (error) {
                console.error(
                    "Card product load error:",
                    error
                );

                setCardProducts([]);
                setCardProductsError(
                    error?.message ||
                    "Unable to load card products."
                );
            } finally {
                setCardProductsLoading(false);
            }
        };

        loadCardProducts();
    }, [selectedBankForCards]);

    const handleBankToggle = (bankId) => {
        const normalizedId = Number(bankId);

        if (selectedBankIds.includes(normalizedId)) {
            onBankSelectionChange(
                selectedBankIds.filter(
                    (id) => id !== normalizedId
                )
            );

            return;
        }

        onBankSelectionChange([
            ...selectedBankIds,
            normalizedId,
        ]);
    };

    const handleCardProductToggle = (cardProductId) => {
        const normalizedId = Number(cardProductId);

        if (
            selectedCardProductIds.includes(
                normalizedId
            )
        ) {
            onCardProductSelectionChange(
                selectedCardProductIds.filter(
                    (id) => id !== normalizedId
                )
            );

            return;
        }

        onCardProductSelectionChange([
            ...selectedCardProductIds,
            normalizedId,
        ]);
    };

    return (
        <section className="offer-form-section">
            <div className="offer-form-section-heading">
                <div>
                    <p className="offer-form-eyebrow">
                        Step 4
                    </p>

                    <h2>Who can use this offer?</h2>

                    <p>
                        Keep it general, restrict it to
                        banks, or target specific card products.
                    </p>
                </div>
            </div>

            <div className="offer-applicability-modes">
                <button
                    type="button"
                    className={`offer-applicability-mode ${mode === "GENERAL"
                            ? "selected"
                            : ""
                        }`}
                    onClick={() =>
                        onModeChange("GENERAL")
                    }
                >
                    <span className="offer-mode-icon">
                        ✦
                    </span>

                    <span>
                        <strong>General offer</strong>

                        <small>
                            Available across eligible purchases
                        </small>
                    </span>
                </button>

                <button
                    type="button"
                    className={`offer-applicability-mode ${mode === "BANKS"
                            ? "selected"
                            : ""
                        }`}
                    onClick={() =>
                        onModeChange("BANKS")
                    }
                >
                    <span className="offer-mode-icon">
                        🏦
                    </span>

                    <span>
                        <strong>Specific banks</strong>

                        <small>
                            Apply to cards from selected banks
                        </small>
                    </span>
                </button>

                <button
                    type="button"
                    className={`offer-applicability-mode ${mode === "CARDS"
                            ? "selected"
                            : ""
                        }`}
                    onClick={() =>
                        onModeChange("CARDS")
                    }
                >
                    <span className="offer-mode-icon">
                        💳
                    </span>

                    <span>
                        <strong>Specific cards</strong>

                        <small>
                            Target selected card products
                        </small>
                    </span>
                </button>
            </div>

            {mode === "GENERAL" && (
                <div className="offer-applicability-message">
                    <span>✦</span>

                    <div>
                        <strong>General offer</strong>

                        <p>
                            No bank or card restriction will
                            be attached to this offer.
                        </p>
                    </div>
                </div>
            )}

            {mode === "BANKS" && (
                <div className="offer-applicability-selection">
                    <div className="offer-selection-heading">
                        <label>
                            Select applicable banks
                        </label>

                        <span>
                            {selectedBankIds.length} selected
                        </span>
                    </div>

                    {banksLoading ? (
                        <div className="offer-selection-loading">
                            Loading banks...
                        </div>
                    ) : banks.length === 0 ? (
                        <div className="offer-selection-empty">
                            No banks available.
                        </div>
                    ) : (
                        <div className="offer-bank-options">
                            {banks.map((bank) => {
                                const selected =
                                    selectedBankIds.includes(
                                        bank.id
                                    );

                                return (
                                    <button
                                        key={bank.id}
                                        type="button"
                                        className={`offer-bank-option ${selected
                                                ? "selected"
                                                : ""
                                            }`}
                                        onClick={() =>
                                            handleBankToggle(
                                                bank.id
                                            )
                                        }
                                    >
                                        <span className="offer-checkbox">
                                            {selected ? "✓" : ""}
                                        </span>

                                        <span>
                                            {bank.name}
                                        </span>
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>
            )}

            {mode === "CARDS" && (
                <div className="offer-applicability-selection">
                    <div className="offer-selection-heading">
                        <label>
                            Select applicable card products
                        </label>

                        <span>
                            {selectedCardProductIds.length} selected
                        </span>
                    </div>

                    <div className="offer-card-bank-selector">
                        <label htmlFor="offer-card-bank">
                            Bank
                        </label>

                        <select
                            id="offer-card-bank"
                            value={selectedBankForCards}
                            onChange={(event) =>
                                setSelectedBankForCards(
                                    event.target.value
                                )
                            }
                            disabled={banksLoading}
                        >
                            <option value="">
                                Select a bank
                            </option>

                            {banks.map((bank) => (
                                <option
                                    key={bank.id}
                                    value={bank.id}
                                >
                                    {bank.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    {selectedBankForCards && (
                        <div className="offer-card-product-options">
                            {cardProductsLoading ? (
                                <div className="offer-selection-loading">
                                    Loading card products...
                                </div>
                            ) : cardProductsError ? (
                                <div className="offer-selection-error">
                                    {cardProductsError}
                                </div>
                            ) : cardProducts.length === 0 ? (
                                <div className="offer-selection-empty">
                                    No active card products found
                                    for this bank.
                                </div>
                            ) : (
                                cardProducts.map(
                                    (cardProduct) => {
                                        const selected =
                                            selectedCardProductIds.includes(
                                                cardProduct.id
                                            );

                                        return (
                                            <button
                                                key={
                                                    cardProduct.id
                                                }
                                                type="button"
                                                className={`offer-card-product-option ${selected
                                                        ? "selected"
                                                        : ""
                                                    }`}
                                                onClick={() =>
                                                    handleCardProductToggle(
                                                        cardProduct.id
                                                    )
                                                }
                                            >
                                                <span className="offer-checkbox">
                                                    {selected
                                                        ? "✓"
                                                        : ""}
                                                </span>

                                                <span>
                                                    <strong>
                                                        {
                                                            cardProduct.name
                                                        }
                                                    </strong>

                                                    <small>
                                                        {
                                                            cardProduct.cardType
                                                        }
                                                    </small>
                                                </span>
                                            </button>
                                        );
                                    }
                                )
                            )}
                        </div>
                    )}

                    {!selectedBankForCards && (
                        <div className="offer-selection-hint">
                            Choose a bank to see its available
                            card products.
                        </div>
                    )}
                </div>
            )}

            {mode === "BANKS" &&
                selectedBankIds.length === 0 && (
                    <p className="offer-field-hint error">
                        Select at least one bank.
                    </p>
                )}

            {mode === "CARDS" &&
                selectedCardProductIds.length === 0 && (
                    <p className="offer-field-hint error">
                        Select at least one card product.
                    </p>
                )}
        </section>
    );
}

export default OfferApplicability;