import { useMemo, useState } from "react";

import OfferApplicability from "./OfferApplicability";

function getToday() {
    return new Date().toISOString().split("T")[0];
}

function getDefaultEndDate() {
    const date = new Date();

    date.setDate(date.getDate() + 30);

    return date.toISOString().split("T")[0];
}

function formatPreviewAmount(value) {
    if (!value) {
        return null;
    }

    const amount = Number(value);

    if (Number.isNaN(amount)) {
        return null;
    }

    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
    }).format(amount);
}

function formatPreviewDiscount(
    discountType,
    discountValue
) {
    if (!discountValue) {
        return "YOUR SAVING";
    }

    const value = Number(discountValue);

    if (Number.isNaN(value)) {
        return "YOUR SAVING";
    }

    if (discountType === "PERCENTAGE") {
        return `${value}% OFF`;
    }

    return `${formatPreviewAmount(value)} OFF`;
}

function AddOfferForm({
    globalMerchants,
    globalMerchantsLoading,
    banks,
    banksLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [title, setTitle] = useState("");
    const [description, setDescription] =
        useState("");

    const [discountType, setDiscountType] =
        useState("PERCENTAGE");

    const [discountValue, setDiscountValue] =
        useState("");

    const [maxDiscount, setMaxDiscount] =
        useState("");

    const [minTransactionAmount, setMinTransactionAmount] =
        useState("");

    const [startDate, setStartDate] =
        useState(getToday());

    const [endDate, setEndDate] =
        useState(getDefaultEndDate());

    const [globalMerchantId, setGlobalMerchantId] =
        useState("");

    const [sourceUrl, setSourceUrl] = useState("");

    const [applicabilityMode, setApplicabilityMode] =
        useState("GENERAL");

    const [selectedBankIds, setSelectedBankIds] =
        useState([]);

    const [
        selectedCardProductIds,
        setSelectedCardProductIds,
    ] = useState([]);

    const [validationError, setValidationError] =
        useState("");

    const previewDiscount = useMemo(
        () =>
            formatPreviewDiscount(
                discountType,
                discountValue
            ),
        [discountType, discountValue]
    );

    const selectedMerchant =
        globalMerchants.find(
            (merchant) =>
                String(merchant.id) ===
                String(globalMerchantId)
        );

    const validateForm = () => {
        if (title.trim().length < 2) {
            return "Offer title must be at least 2 characters.";
        }

        if (title.trim().length > 150) {
            return "Offer title cannot exceed 150 characters.";
        }

        if (description.length > 500) {
            return "Description cannot exceed 500 characters.";
        }

        if (
            !discountValue ||
            Number(discountValue) <= 0
        ) {
            return "Enter a discount value greater than 0.";
        }

        if (!startDate || !endDate) {
            return "Start date and end date are required.";
        }

        if (endDate < startDate) {
            return "End date cannot be before start date.";
        }

        if (
            maxDiscount &&
            Number(maxDiscount) <= 0
        ) {
            return "Maximum discount must be greater than 0.";
        }

        if (
            minTransactionAmount &&
            Number(minTransactionAmount) <= 0
        ) {
            return "Minimum transaction amount must be greater than 0.";
        }

        if (
            applicabilityMode === "BANKS" &&
            selectedBankIds.length === 0
        ) {
            return "Select at least one applicable bank.";
        }

        if (
            applicabilityMode === "CARDS" &&
            selectedCardProductIds.length === 0
        ) {
            return "Select at least one applicable card product.";
        }

        if (sourceUrl.trim()) {
            try {
                new URL(sourceUrl.trim());
            } catch {
                return "Please enter a valid source URL.";
            }
        }

        return "";
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setValidationError("");

        const error = validateForm();

        if (error) {
            setValidationError(error);
            return;
        }

        const offer = {
            title: title.trim(),
            description:
                description.trim() || null,
            discountType,
            discountValue: Number(discountValue),
            maxDiscount: maxDiscount
                ? Number(maxDiscount)
                : null,
            minTransactionAmount:
                minTransactionAmount
                    ? Number(minTransactionAmount)
                    : null,
            startDate,
            endDate,
            globalMerchantId:
                globalMerchantId
                    ? Number(globalMerchantId)
                    : null,
            sourceUrl: sourceUrl.trim() || null,
        };

        await onSubmit({
            offer,
            applicabilityMode,
            bankIds: selectedBankIds,
            cardProductIds:
                selectedCardProductIds,
        });
    };

    return (
        <section className="add-offer-form">
            <div className="add-offer-form-header">
                <div>
                    <p className="offers-eyebrow">
                        Create a savings opportunity
                    </p>

                    <h2>Add Offer</h2>

                    <p>
                        Tell LinkVault what the offer is and
                        who can use it.
                    </p>
                </div>

                <button
                    type="button"
                    className="offer-form-close"
                    onClick={onCancel}
                    disabled={submitting}
                    aria-label="Close add offer form"
                >
                    ×
                </button>
            </div>

            {validationError && (
                <div className="offer-form-validation">
                    {validationError}
                </div>
            )}

            <div className="add-offer-layout">
                <form
                    className="offer-form"
                    onSubmit={handleSubmit}
                >
                    <section className="offer-form-section">
                        <div className="offer-form-section-heading">
                            <div>
                                <p className="offer-form-eyebrow">
                                    Step 1
                                </p>

                                <h2>Offer details</h2>

                                <p>
                                    Give your offer a clear,
                                    recognizable name.
                                </p>
                            </div>
                        </div>

                        <div className="offer-form-grid">
                            <div className="offer-field offer-field-full">
                                <label htmlFor="offer-title">
                                    Offer title *
                                </label>

                                <input
                                    id="offer-title"
                                    type="text"
                                    value={title}
                                    maxLength={150}
                                    placeholder="e.g. Weekend shopping discount"
                                    onChange={(event) =>
                                        setTitle(
                                            event.target.value
                                        )
                                    }
                                />

                                <span className="offer-field-hint">
                                    {title.length}/150
                                </span>
                            </div>

                            <div className="offer-field offer-field-full">
                                <label htmlFor="offer-description">
                                    Description
                                </label>

                                <textarea
                                    id="offer-description"
                                    value={description}
                                    maxLength={500}
                                    rows={3}
                                    placeholder="Add useful context about this offer..."
                                    onChange={(event) =>
                                        setDescription(
                                            event.target.value
                                        )
                                    }
                                />

                                <span className="offer-field-hint">
                                    {description.length}/500
                                </span>
                            </div>

                            <div className="offer-field offer-field-full">
                                <label htmlFor="source-url">
                                    Source URL
                                </label>

                                <input
                                    id="source-url"
                                    type="url"
                                    value={sourceUrl}
                                    onChange={(event) =>
                                        setSourceUrl(event.target.value)
                                    }
                                    maxLength={2048}
                                    placeholder="https://example.com/offer"
                                />

                                <span className="offer-field-hint">
                                    Optional. Link to the original page where this offer was found.
                                </span>
                            </div>

                            <div className="offer-field">
                                <label htmlFor="offer-merchant">
                                    Merchant
                                </label>

                                <select
                                    id="offer-merchant"
                                    value={
                                        globalMerchantId
                                    }
                                    onChange={(event) =>
                                        setGlobalMerchantId(
                                            event.target.value
                                        )
                                    }
                                    disabled={
                                        globalMerchantsLoading
                                    }
                                >
                                    <option value="">
                                        General offer
                                    </option>

                                    {globalMerchants.map(
                                        (merchant) => (
                                            <option
                                                key={
                                                    merchant.id
                                                }
                                                value={
                                                    merchant.id
                                                }
                                            >
                                                {
                                                    merchant.name
                                                }
                                            </option>
                                        )
                                    )}
                                </select>

                                <span className="offer-field-hint">
                                    Leave as General offer if
                                    this isn't tied to a specific
                                    merchant.
                                </span>
                            </div>
                        </div>
                    </section>

                    <section className="offer-form-section">
                        <div className="offer-form-section-heading">
                            <div>
                                <p className="offer-form-eyebrow">
                                    Step 2
                                </p>

                                <h2>Discount</h2>

                                <p>
                                    Define exactly what the user
                                    can save.
                                </p>
                            </div>
                        </div>

                        <div className="offer-discount-builder">
                            <div className="offer-discount-type">
                                <button
                                    type="button"
                                    className={
                                        discountType ===
                                            "PERCENTAGE"
                                            ? "selected"
                                            : ""
                                    }
                                    onClick={() =>
                                        setDiscountType(
                                            "PERCENTAGE"
                                        )
                                    }
                                >
                                    % Percentage
                                </button>

                                <button
                                    type="button"
                                    className={
                                        discountType ===
                                            "FLAT"
                                            ? "selected"
                                            : ""
                                    }
                                    onClick={() =>
                                        setDiscountType(
                                            "FLAT"
                                        )
                                    }
                                >
                                    ₹ Flat amount
                                </button>
                            </div>

                            <div className="offer-form-grid">
                                <div className="offer-field">
                                    <label htmlFor="discount-value">
                                        Discount value *
                                    </label>

                                    <div className="offer-input-prefix">
                                        <span>
                                            {discountType ===
                                                "PERCENTAGE"
                                                ? "%"
                                                : "₹"}
                                        </span>

                                        <input
                                            id="discount-value"
                                            type="number"
                                            min="0.01"
                                            step="0.01"
                                            value={
                                                discountValue
                                            }
                                            placeholder={
                                                discountType ===
                                                    "PERCENTAGE"
                                                    ? "15"
                                                    : "500"
                                            }
                                            onChange={(
                                                event
                                            ) =>
                                                setDiscountValue(
                                                    event
                                                        .target
                                                        .value
                                                )
                                            }
                                        />
                                    </div>
                                </div>

                                <div className="offer-field">
                                    <label htmlFor="max-discount">
                                        Maximum discount
                                    </label>

                                    <div className="offer-input-prefix">
                                        <span>₹</span>

                                        <input
                                            id="max-discount"
                                            type="number"
                                            min="0.01"
                                            step="0.01"
                                            value={
                                                maxDiscount
                                            }
                                            placeholder="1500"
                                            onChange={(
                                                event
                                            ) =>
                                                setMaxDiscount(
                                                    event
                                                        .target
                                                        .value
                                                )
                                            }
                                        />
                                    </div>
                                </div>

                                <div className="offer-field">
                                    <label htmlFor="min-spend">
                                        Minimum transaction amount
                                    </label>

                                    <div className="offer-input-prefix">
                                        <span>₹</span>

                                        <input
                                            id="min-spend"
                                            type="number"
                                            min="0.01"
                                            step="0.01"
                                            value={
                                                minTransactionAmount
                                            }
                                            placeholder="2000"
                                            onChange={(
                                                event
                                            ) =>
                                                setMinTransactionAmount(
                                                    event
                                                        .target
                                                        .value
                                                )
                                            }
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section className="offer-form-section">
                        <div className="offer-form-section-heading">
                            <div>
                                <p className="offer-form-eyebrow">
                                    Step 3
                                </p>

                                <h2>Validity</h2>

                                <p>
                                    Set when this opportunity is
                                    available.
                                </p>
                            </div>
                        </div>

                        <div className="offer-form-grid">
                            <div className="offer-field">
                                <label htmlFor="offer-start-date">
                                    Start date *
                                </label>

                                <input
                                    id="offer-start-date"
                                    type="date"
                                    value={startDate}
                                    onChange={(event) =>
                                        setStartDate(
                                            event.target.value
                                        )
                                    }
                                />
                            </div>

                            <div className="offer-field">
                                <label htmlFor="offer-end-date">
                                    End date *
                                </label>

                                <input
                                    id="offer-end-date"
                                    type="date"
                                    min={startDate}
                                    value={endDate}
                                    onChange={(event) =>
                                        setEndDate(
                                            event.target.value
                                        )
                                    }
                                />
                            </div>
                        </div>
                    </section>

                    <OfferApplicability
                        banks={banks}
                        banksLoading={banksLoading}
                        mode={applicabilityMode}
                        selectedBankIds={
                            selectedBankIds
                        }
                        selectedCardProductIds={
                            selectedCardProductIds
                        }
                        onModeChange={
                            setApplicabilityMode
                        }
                        onBankSelectionChange={
                            setSelectedBankIds
                        }
                        onCardProductSelectionChange={
                            setSelectedCardProductIds
                        }
                    />

                    <div className="offer-form-actions">
                        <button
                            type="button"
                            className="offer-cancel-button"
                            onClick={onCancel}
                            disabled={submitting}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="offer-save-button"
                            disabled={submitting}
                        >
                            {submitting
                                ? "Saving offer..."
                                : "Save Offer"}
                        </button>
                    </div>
                </form>

                <aside className="offer-live-preview">
                    <div className="offer-preview-label">
                        LIVE PREVIEW
                    </div>

                    <div className="offer-preview-card">
                        <div className="offer-preview-top">
                            <strong>
                                {previewDiscount}
                            </strong>

                            <span>PREVIEW</span>
                        </div>

                        <div className="offer-preview-body">
                            <p className="offer-preview-eyebrow">
                                {selectedMerchant
                                    ? "MERCHANT OFFER"
                                    : "GENERAL OFFER"}
                            </p>

                            <h3>
                                {title.trim() ||
                                    "Your offer title"}
                            </h3>

                            <p>
                                {description.trim() ||
                                    "Your offer description will appear here."}
                            </p>

                            <div className="offer-preview-divider" />

                            <div className="offer-preview-detail">
                                <span>
                                    {selectedMerchant
                                        ? "Merchant"
                                        : "Availability"}
                                </span>

                                <strong>
                                    {selectedMerchant
                                        ? selectedMerchant.name
                                        : "Available across eligible purchases"}
                                </strong>
                            </div>

                            {minTransactionAmount && (
                                <div className="offer-preview-detail">
                                    <span>
                                        Minimum spend
                                    </span>

                                    <strong>
                                        {formatPreviewAmount(
                                            minTransactionAmount
                                        )}
                                    </strong>
                                </div>
                            )}

                            {maxDiscount && (
                                <div className="offer-preview-detail">
                                    <span>
                                        Maximum saving
                                    </span>

                                    <strong>
                                        {formatPreviewAmount(
                                            maxDiscount
                                        )}
                                    </strong>
                                </div>
                            )}

                            <div className="offer-preview-validity">
                                <span>Valid</span>

                                <strong>
                                    {startDate || "Start"}
                                    {" — "}
                                    {endDate || "End"}
                                </strong>
                            </div>
                        </div>
                    </div>

                    <p className="offer-preview-note">
                        This preview updates as you build the
                        offer. It is not saved separately.
                    </p>
                </aside>
            </div>
        </section>
    );
}

export default AddOfferForm;