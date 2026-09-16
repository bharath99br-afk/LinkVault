import { useState } from "react";
import {
    isValidMerchantUrl,
    normalizeMerchantUrl,
} from "../../utils/merchantUtils";

const INITIAL_FORM = {
    name: "",
    websiteUrl: "",
    globalMerchantId: "",
};

function AddMerchantForm({
    globalMerchants,
    globalMerchantsLoading,
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
        const websiteUrl = form.websiteUrl.trim();

        if (!name) {
            nextErrors.name = "Please enter a merchant name.";
        } else if (name.length < 2) {
            nextErrors.name =
                "Merchant name must be at least 2 characters.";
        } else if (name.length > 100) {
            nextErrors.name =
                "Merchant name cannot exceed 100 characters.";
        }

        if (!websiteUrl) {
            nextErrors.websiteUrl =
                "Website URL is required.";
        } else if (!isValidMerchantUrl(websiteUrl)) {
            nextErrors.websiteUrl =
                "Please enter a valid website URL.";
        }

        setErrors(nextErrors);

        return Object.keys(nextErrors).length === 0;
    };

    const validateField = (field, value) => {
        const nextErrors = { ...errors };

        const trimmedValue = value.trim();

        if (field === "name") {
            if (!trimmedValue) {
                nextErrors.name =
                    "Please enter a merchant name.";
            } else if (trimmedValue.length < 2) {
                nextErrors.name =
                    "Merchant name must be at least 2 characters.";
            } else if (trimmedValue.length > 100) {
                nextErrors.name =
                    "Merchant name cannot exceed 100 characters.";
            } else {
                delete nextErrors.name;
            }
        }

        if (field === "websiteUrl") {
            if (!trimmedValue) {
                nextErrors.websiteUrl =
                    "Website URL is required.";
            } else if (!isValidMerchantUrl(trimmedValue)) {
                nextErrors.websiteUrl =
                    "Please enter a valid website URL.";
            } else {
                delete nextErrors.websiteUrl;
            }
        }

        setErrors(nextErrors);
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!validate()) {
            return;
        }

        const normalized = {
            name: form.name.trim(),
            websiteUrl: normalizeMerchantUrl(
                form.websiteUrl
            ),
            globalMerchantId: form.globalMerchantId
                ? Number(form.globalMerchantId)
                : null,
        };

        onSubmit(normalized);
    };

    return (
        <section className="merchant-form-card">
            <div className="merchant-form-heading">
                <div>
                    <p className="merchant-form-eyebrow">
                        Add to your vault
                    </p>

                    <h2>Add merchant</h2>

                    <p>
                        Save a shopping destination and
                        optionally connect it to LinkVault's
                        merchant directory.
                    </p>
                </div>
            </div>

            <form
                className="merchant-form"
                onSubmit={handleSubmit}
                noValidate
            >
                <div className="merchant-form-grid">
                    <div className="merchant-form-group">
                        <label htmlFor="merchant-name">
                            Merchant name
                        </label>

                        <input
                            id="merchant-name"
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
                            className={
                                errors.name
                                    ? "merchant-input-error"
                                    : ""
                            }
                        />

                        {errors.name && (
                            <span className="merchant-field-error">
                                {errors.name}
                            </span>
                        )}
                    </div>

                    <div className="merchant-form-group">
                        <label htmlFor="merchant-website">
                            Website
                        </label>

                        <input
                            id="merchant-website"
                            type="text"
                            value={form.websiteUrl}
                            onChange={(event) =>
                                updateField(
                                    "websiteUrl",
                                    event.target.value
                                )
                            }
                            onBlur={(event) =>
                                validateField(
                                    "websiteUrl",
                                    event.target.value
                                )
                            }
                            className={
                                errors.websiteUrl
                                    ? "merchant-input-error"
                                    : ""
                            }
                        />

                        {errors.websiteUrl && (
                            <span className="merchant-field-error">
                                {errors.websiteUrl}
                            </span>
                        )}
                    </div>

                    <div className="merchant-form-group merchant-form-full">
                        <label htmlFor="merchant-global">
                            LinkVault identity
                            <span className="merchant-label-optional">
                                Optional
                            </span>
                        </label>

                        <select
                            id="merchant-global"
                            value={form.globalMerchantId}
                            onChange={(event) =>
                                updateField(
                                    "globalMerchantId",
                                    event.target.value
                                )
                            }
                            disabled={
                                globalMerchantsLoading ||
                                submitting
                            }
                        >
                            <option value="">
                                {globalMerchantsLoading
                                    ? "Loading merchant directory..."
                                    : "Let LinkVault identify it automatically"}
                            </option>

                            {globalMerchants.map(
                                (merchant) => (
                                    <option
                                        key={merchant.id}
                                        value={merchant.id}
                                    >
                                        {merchant.name}
                                    </option>
                                )
                            )}
                        </select>

                        <p className="merchant-field-help">
                            Optional. LinkVault can match your
                            merchant automatically when possible.
                        </p>
                    </div>
                </div>

                <div className="merchant-form-actions">
                    <button
                        type="button"
                        className="merchant-secondary-button"
                        onClick={onCancel}
                        disabled={submitting}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="merchant-primary-button"
                        disabled={submitting}
                    >
                        {submitting
                            ? "Saving..."
                            : "Save Merchant"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default AddMerchantForm;