import { useEffect, useState } from "react";

import { getCardProducts } from "../../services/cardProductService";

function EditCardForm({
    card,
    banks,
    banksLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [form, setForm] = useState({
        name: "",
        lastFourDigits: "",
        cardType: "",
        bankId: "",
        cardProductId: "",
        customCard: false,
    });

    const [cardProducts, setCardProducts] = useState([]);
    const [cardProductsLoading, setCardProductsLoading] =
        useState(false);
    const [cardProductsError, setCardProductsError] =
        useState("");
    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (!card) {
            return;
        }

        setForm({
            name: card.name || "",
            lastFourDigits:
                card.lastFourDigits || "",
            cardType: card.cardType || "",
            bankId: card.bankId
                ? String(card.bankId)
                : "",
            cardProductId: card.cardProductId
                ? String(card.cardProductId)
                : "",
            customCard: !card.cardProductId,
        });

        setErrors({});
    }, [card]);

    useEffect(() => {
        if (
            !form.bankId ||
            form.customCard
        ) {
            setCardProducts([]);
            setCardProductsLoading(false);
            setCardProductsError("");
            return;
        }

        const loadCardProducts = async () => {
            setCardProductsLoading(true);
            setCardProductsError("");

            try {
                const response = await getCardProducts({
                    bankId: form.bankId,
                    page: 0,
                    size: 50,
                });

                setCardProducts(
                    response.data.content || []
                );
            } catch (error) {
                console.error(
                    "Card product load error:",
                    error
                );

                setCardProducts([]);
                setCardProductsError(
                    error.data?.message ||
                    "Failed to load card products."
                );
            } finally {
                setCardProductsLoading(false);
            }
        };

        loadCardProducts();
    }, [
        form.bankId,
        form.customCard,
    ]);

    const updateField = (field, value) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));

        setErrors((current) => ({
            ...current,
            [field]: "",
            form: "",
        }));
    };

    const handleBankChange = (value) => {
        setForm((current) => ({
            ...current,
            bankId: value,
            cardProductId: "",
            name: "",
            cardType: "CREDIT",
            customCard: false,
        }));

        setErrors((current) => ({
            ...current,
            bankId: "",
            cardProductId: "",
            name: "",
            cardType: "",
            form: "",
        }));
    };

    const handleCardProductChange = (value) => {
        const selectedProduct =
            cardProducts.find(
                (product) =>
                    String(product.id) === value
            );

        if (!selectedProduct) {
            setForm((current) => ({
                ...current,
                cardProductId: "",
                name: "",
                cardType: "CREDIT",
            }));

            return;
        }

        setForm((current) => ({
            ...current,
            cardProductId: value,
            name: selectedProduct.name,
            cardType: selectedProduct.cardType,
            customCard: false,
        }));

        setErrors((current) => ({
            ...current,
            cardProductId: "",
            name: "",
            cardType: "",
            form: "",
        }));
    };

    const handleCustomCardToggle = () => {
        setForm((current) => ({
            ...current,
            customCard: !current.customCard,
            cardProductId: "",
            name: "",
            cardType: "CREDIT",
        }));

        setErrors({});
    };

    const validate = () => {
        const nextErrors = {};

        const name = form.name.trim();
        const lastFour =
            form.lastFourDigits.trim();

        if (!name) {
            nextErrors.name =
                "Please enter a card name.";
        } else if (name.length < 2) {
            nextErrors.name =
                "Card name must be at least 2 characters.";
        } else if (name.length > 100) {
            nextErrors.name =
                "Card name cannot exceed 100 characters.";
        }

        if (!/^\d{4}$/.test(lastFour)) {
            nextErrors.lastFourDigits =
                "Please enter exactly 4 digits.";
        }

        if (!form.cardType) {
            nextErrors.cardType =
                "Please select a card type.";
        }

        if (!form.bankId) {
            nextErrors.bankId =
                "Please select a bank.";
        }

        if (
            !form.customCard &&
            !form.cardProductId
        ) {
            nextErrors.cardProductId =
                "Please select a card product.";
        }

        setErrors(nextErrors);

        return Object.keys(nextErrors).length === 0;
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!validate()) {
            return;
        }

        const normalized = {
            name: form.name.trim(),
            lastFourDigits:
                form.lastFourDigits.trim(),
            cardType: form.cardType,
            bankId: Number(form.bankId),
            cardProductId: form.customCard
                ? null
                : Number(form.cardProductId),
        };

        const original = {
            name: card.name || "",
            lastFourDigits:
                card.lastFourDigits || "",
            cardType: card.cardType || "",
            bankId: card.bankId || null,
            cardProductId:
                card.cardProductId || null,
        };

        const unchanged =
            normalized.name === original.name &&
            normalized.lastFourDigits ===
            original.lastFourDigits &&
            normalized.cardType ===
            original.cardType &&
            normalized.bankId ===
            original.bankId &&
            normalized.cardProductId ===
            original.cardProductId;

        if (unchanged) {
            setErrors({
                form: "No changes to save.",
            });

            return;
        }

        onSubmit(normalized);
    };

    return (
        <section className="card-form-card">
            <div className="card-form-heading">
                <p className="card-form-eyebrow">
                    Card details
                </p>

                <h2>Edit card</h2>

                <p>
                    Keep your saved card information
                    accurate.
                </p>
            </div>

            {errors.form && (
                <div className="card-inline-error">
                    {errors.form}
                </div>
            )}

            <form
                className="card-form"
                onSubmit={handleSubmit}
                noValidate
            >
                <div className="card-form-grid">

                    {/* Bank */}
                    <div className="card-form-group">
                        <label htmlFor="edit-card-bank">
                            Bank
                        </label>

                        <select
                            id="edit-card-bank"
                            value={form.bankId}
                            onChange={(event) =>
                                handleBankChange(
                                    event.target.value
                                )
                            }
                            disabled={
                                banksLoading ||
                                submitting
                            }
                            className={
                                errors.bankId
                                    ? "card-input-error"
                                    : ""
                            }
                        >
                            <option value="">
                                {banksLoading
                                    ? "Loading banks..."
                                    : "Select bank"}
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

                        {errors.bankId && (
                            <span className="card-field-error">
                                {errors.bankId}
                            </span>
                        )}
                    </div>

                    {/* Card Product */}
                    <div className="card-form-group">
                        <label htmlFor="edit-card-product">
                            Card product
                        </label>

                        <select
                            id="edit-card-product"
                            value={
                                form.cardProductId
                            }
                            onChange={(event) =>
                                handleCardProductChange(
                                    event.target.value
                                )
                            }
                            disabled={
                                !form.bankId ||
                                form.customCard ||
                                cardProductsLoading ||
                                submitting
                            }
                            className={
                                errors.cardProductId
                                    ? "card-input-error"
                                    : ""
                            }
                        >
                            <option value="">
                                {!form.bankId
                                    ? "Select bank first"
                                    : cardProductsLoading
                                        ? "Loading card products..."
                                        : "Select card product"}
                            </option>

                            {cardProducts.map(
                                (product) => (
                                    <option
                                        key={product.id}
                                        value={product.id}
                                    >
                                        {product.name}
                                    </option>
                                )
                            )}
                        </select>

                        {errors.cardProductId && (
                            <span className="card-field-error">
                                {errors.cardProductId}
                            </span>
                        )}

                        {cardProductsError && (
                            <span className="card-field-error">
                                {cardProductsError}
                            </span>
                        )}

                        {!cardProductsLoading &&
                            !cardProductsError &&
                            form.bankId &&
                            !form.customCard &&
                            cardProducts.length === 0 && (
                                <p className="card-field-help">
                                    No card products are currently
                                    listed for this bank.
                                </p>
                            )}

                        <button
                            type="button"
                            className="card-custom-toggle"
                            onClick={
                                handleCustomCardToggle
                            }
                            disabled={submitting}
                        >
                            {form.customCard
                                ? "← Choose a listed card"
                                : "Card not listed?"}
                        </button>
                    </div>

                    {/* Custom Card Name */}
                    {form.customCard && (
                        <div className="card-form-group">
                            <label htmlFor="edit-card-name">
                                Custom card name
                            </label>

                            <input
                                id="edit-card-name"
                                type="text"
                                value={form.name}
                                onChange={(event) =>
                                    updateField(
                                        "name",
                                        event.target.value
                                    )
                                }
                                maxLength={100}
                                disabled={submitting}
                                className={
                                    errors.name
                                        ? "card-input-error"
                                        : ""
                                }
                            />

                            {errors.name && (
                                <span className="card-field-error">
                                    {errors.name}
                                </span>
                            )}

                            <p className="card-field-help">
                                This card is currently saved as
                                a custom card.
                            </p>
                        </div>
                    )}

                    {/* Last 4 */}
                    <div className="card-form-group">
                        <label htmlFor="edit-card-last-four">
                            Last 4 digits
                        </label>

                        <input
                            id="edit-card-last-four"
                            type="text"
                            inputMode="numeric"
                            maxLength={4}
                            value={
                                form.lastFourDigits
                            }
                            onChange={(event) =>
                                updateField(
                                    "lastFourDigits",
                                    event.target.value
                                        .replace(/\D/g, "")
                                        .slice(0, 4)
                                )
                            }
                            disabled={submitting}
                            className={
                                errors.lastFourDigits
                                    ? "card-input-error"
                                    : ""
                            }
                        />

                        {errors.lastFourDigits && (
                            <span className="card-field-error">
                                {errors.lastFourDigits}
                            </span>
                        )}
                    </div>

                    {/* Card Type */}
                    <div className="card-form-group">
                        <label htmlFor="edit-card-type">
                            Card type
                        </label>

                        <select
                            id="edit-card-type"
                            value={form.cardType}
                            onChange={(event) =>
                                updateField(
                                    "cardType",
                                    event.target.value
                                )
                            }
                            disabled={
                                !form.customCard ||
                                submitting
                            }
                        >
                            <option value="CREDIT">
                                Credit Card
                            </option>

                            <option value="DEBIT">
                                Debit Card
                            </option>
                        </select>

                        {!form.customCard && (
                            <p className="card-field-help">
                                Card type is determined by the
                                selected card product.
                            </p>
                        )}

                        {errors.cardType && (
                            <span className="card-field-error">
                                {errors.cardType}
                            </span>
                        )}
                    </div>
                </div>

                <div className="card-form-actions">
                    <button
                        type="button"
                        className="card-secondary-button"
                        onClick={onCancel}
                        disabled={submitting}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="card-primary-button"
                        disabled={
                            submitting ||
                            cardProductsLoading
                        }
                    >
                        {submitting
                            ? "Updating..."
                            : "Update Card"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default EditCardForm;