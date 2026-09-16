import { useEffect, useState } from "react";

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
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (!card) {
            return;
        }

        setForm({
            name: card.name || "",
            lastFourDigits: card.lastFourDigits || "",
            cardType: card.cardType || "",
            bankId: card.bankId
                ? String(card.bankId)
                : "",
        });

        setErrors({});
    }, [card]);

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

    const validate = () => {
        const nextErrors = {};

        const name = form.name.trim();
        const lastFour = form.lastFourDigits.trim();

        if (!name) {
            nextErrors.name = "Please enter a card name.";
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
            lastFourDigits: form.lastFourDigits.trim(),
            cardType: form.cardType,
            bankId: Number(form.bankId),
        };

        const original = {
            name: card.name || "",
            lastFourDigits: card.lastFourDigits || "",
            cardType: card.cardType || "",
            bankId: card.bankId || null,
        };

        const unchanged =
            normalized.name === original.name &&
            normalized.lastFourDigits ===
            original.lastFourDigits &&
            normalized.cardType === original.cardType &&
            normalized.bankId === original.bankId;

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
                    Keep your saved card information accurate.
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
                    <div className="card-form-group">
                        <label htmlFor="edit-card-name">
                            Card name
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
                    </div>

                    <div className="card-form-group">
                        <label htmlFor="edit-card-bank">
                            Bank
                        </label>

                        <select
                            id="edit-card-bank"
                            value={form.bankId}
                            onChange={(event) =>
                                updateField(
                                    "bankId",
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

                    <div className="card-form-group">
                        <label htmlFor="edit-card-last-four">
                            Last 4 digits
                        </label>

                        <input
                            id="edit-card-last-four"
                            type="text"
                            inputMode="numeric"
                            maxLength={4}
                            value={form.lastFourDigits}
                            onChange={(event) =>
                                updateField(
                                    "lastFourDigits",
                                    event.target.value.replace(
                                        /\D/g,
                                        ""
                                    ).slice(0, 4)
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
                            disabled={submitting}
                        >
                            <option value="CREDIT">
                                Credit Card
                            </option>

                            <option value="DEBIT">
                                Debit Card
                            </option>
                        </select>
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
                        disabled={submitting}
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