import { useState } from "react";

const INITIAL_FORM = {
    name: "",
    lastFourDigits: "",
    cardType: "CREDIT",
    bankId: "",
};

function AddCardForm({
    banks,
    banksLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [form, setForm] = useState(INITIAL_FORM);
    const [errors, setErrors] = useState({});

    const updateField = (field, value) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));

        setErrors((current) => ({
            ...current,
            [field]: "",
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
            lastFourDigits: form.lastFourDigits.trim(),
            cardType: form.cardType,
            bankId: Number(form.bankId),
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
                    Save a card so LinkVault can match it
                    against eligible offers.
                </p>
            </div>

            <form
                className="card-form"
                onSubmit={handleSubmit}
                noValidate
            >
                <div className="card-form-grid">
                    <div className="card-form-group">
                        <label htmlFor="card-name">
                            Card name
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
                            placeholder="e.g. HDFC Regalia"
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
                        <label htmlFor="card-bank">
                            Bank
                        </label>

                        <select
                            id="card-bank"
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
                                    event.target.value.replace(
                                        /\D/g,
                                        ""
                                    ).slice(0, 4)
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
                            ? "Saving..."
                            : "Save Card"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default AddCardForm;