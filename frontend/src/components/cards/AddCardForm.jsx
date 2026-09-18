import { useEffect, useState } from "react";

import { getCardProducts } from "../../services/cardProductService";

const INITIAL_FORM = {
    bankId: "",
    cardProductId: "",
    customCard: false,
    name: "",
    lastFourDigits: "",
    cardType: "CREDIT",
};

function AddCardForm({
    banks,
    banksLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [form, setForm] = useState(INITIAL_FORM);
    const [cardProducts, setCardProducts] = useState([]);
    const [cardProductsLoading, setCardProductsLoading] =
        useState(false);
    const [cardProductsError, setCardProductsError] =
        useState("");
    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (!form.bankId || form.customCard) {
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

                setCardProducts(response.data.content || []);
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
    }, [form.bankId, form.customCard]);

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
        }));

        setErrors((current) => ({
            ...current,
            bankId: "",
            cardProductId: "",
            name: "",
            form: "",
        }));
    };

    const handleCardProductChange = (value) => {
        const selectedProduct = cardProducts.find(
            (product) => String(product.id) === value
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
        const lastFour = form.lastFourDigits.trim();

        if (!form.bankId) {
            nextErrors.bankId =
                "Please select a bank.";
        }

        if (form.customCard) {
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

            if (!form.cardType) {
                nextErrors.cardType =
                    "Please select a card type.";
            }
        } else if (!form.cardProductId) {
            nextErrors.cardProductId =
                "Please select a card product.";
        }

        if (!/^\d{4}$/.test(lastFour)) {
            nextErrors.lastFourDigits =
                "Please enter exactly 4 digits.";
        }

        setErrors(nextErrors);

        return Object.keys(nextErrors).length === 0;
    };

    const validateField = (field, value) => {
        const nextErrors = {
            ...errors,
        };

        const trimmedValue = value.trim();

        if (field === "name") {
            if (!trimmedValue) {
                nextErrors.name =
                    "Please enter a card name.";
            } else if (trimmedValue.length < 2) {
                nextErrors.name =
                    "Card name must be at least 2 characters.";
            } else if (trimmedValue.length > 100) {
                nextErrors.name =
                    "Card name cannot exceed 100 characters.";
            } else {
                delete nextErrors.name;
            }
        }

        if (field === "lastFourDigits") {
            if (!/^\d{4}$/.test(trimmedValue)) {
                nextErrors.lastFourDigits =
                    "Please enter exactly 4 digits.";
            } else {
                delete nextErrors.lastFourDigits;
            }
        }

        setErrors(nextErrors);
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!validate()) {
            return;
        }

        onSubmit({
            name: form.name.trim(),
            lastFourDigits:
                form.lastFourDigits.trim(),
            cardType: form.cardType,
            bankId: Number(form.bankId),
            cardProductId: form.customCard
                ? null
                : Number(form.cardProductId),
        });
    };

    return (
        <section className="card-form-card">
            <div className="card-form-heading">
                <p className="card-form-eyebrow">
                    Add to your wallet
                </p>

                <h2>Add card</h2>

                <p>
                    Choose your bank and card product so
                    LinkVault can match your card accurately
                    against eligible offers.
                </p>
            </div>

            <form
                className="card-form"
                onSubmit={handleSubmit}
                noValidate
            >
                <div className="card-form-grid">

                    {/* Bank */}
                    <div className="card-form-group">
                        <label htmlFor="card-bank">
                            Bank
                        </label>

                        <select
                            id="card-bank"
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
                        <label htmlFor="card-product">
                            Card product
                        </label>

                        <select
                            id="card-product"
                            value={form.cardProductId}
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
                            disabled={
                                submitting
                            }
                        >
                            {form.customCard
                                ? "← Choose a listed card"
                                : "Card not listed?"}
                        </button>
                    </div>

                    {/* Custom Card Name */}
                    {form.customCard && (
                        <div className="card-form-group">
                            <label htmlFor="card-name">
                                Custom card name
                            </label>

                            <input
                                id="card-name"
                                type="text"
                                value={form.name}
                                onChange={(event) =>
                                    updateField(
                                        "name",
                                        event.target.value
                                    )
                                }
                                onBlur={(event) =>
                                    validateField(
                                        "name",
                                        event.target.value
                                    )
                                }
                                maxLength={100}
                                disabled={submitting}
                                placeholder="e.g. HDFC Diners Club"
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
                                This card will be saved as a custom
                                card because it is not in the LinkVault
                                catalogue yet.
                            </p>
                        </div>
                    )}

                    {/* Last 4 */}
                    <div className="card-form-group">
                        <label htmlFor="card-last-four">
                            Last 4 digits
                        </label>

                        <input
                            id="card-last-four"
                            type="text"
                            inputMode="numeric"
                            maxLength={4}
                            value={form.lastFourDigits}
                            onChange={(event) =>
                                updateField(
                                    "lastFourDigits",
                                    event.target.value
                                        .replace(/\D/g, "")
                                        .slice(0, 4)
                                )
                            }
                            onBlur={(event) =>
                                validateField(
                                    "lastFourDigits",
                                    event.target.value
                                )
                            }
                            disabled={submitting}
                            placeholder="4821"
                            className={
                                errors.lastFourDigits
                                    ? "card-input-error"
                                    : ""
                            }
                        />

                        <p className="card-field-help">
                            Only the last 4 digits are stored
                            for identification.
                        </p>

                        {errors.lastFourDigits && (
                            <span className="card-field-error">
                                {errors.lastFourDigits}
                            </span>
                        )}
                    </div>

                    {/* Card Type */}
                    <div className="card-form-group">
                        <label htmlFor="card-type">
                            Card type
                        </label>

                        <select
                            id="card-type"
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
                            ? "Saving..."
                            : "Save Card"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default AddCardForm;