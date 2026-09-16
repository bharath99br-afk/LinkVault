import { useEffect, useState } from "react";
import {
    isValidMerchantUrl,
    normalizeMerchantUrl,
} from "../../utils/merchantUtils";

function EditMerchantForm({
    merchant,
    globalMerchants,
    globalMerchantsLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [form, setForm] = useState({
        name: "",
        websiteUrl: "",
        globalMerchantId: "",
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (!merchant) {
            return;
        }

        setForm({
            name: merchant.name || "",
            websiteUrl: merchant.websiteUrl || "",
            globalMerchantId:
                merchant.globalMerchantId
                    ? String(merchant.globalMerchantId)
                    : "",
        });

        setErrors({});
    }, [merchant]);

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
        const websiteUrl = form.websiteUrl.trim();

        if (!name) {
            nextErrors.name =
                "Please enter a merchant name.";
        } else if (name.length < 2) {
            nextErrors.name =
                "Merchant name must be at least 2 characters.";
        } else if (name.length > 100) {
            nextErrors.name =
                "Merchant name cannot exceed 100 characters.";
        }

        if (!websiteUrl) {
            nextErrors.websiteUrl =
                "Please enter the merchant website URL.";
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
                    "Please enter the merchant website URL.";
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

        const original = {
            name: merchant.name || "",
            websiteUrl: normalizeMerchantUrl(
                merchant.websiteUrl || ""
            ),
            globalMerchantId: merchant.globalMerchantId
                ? Number(merchant.globalMerchantId)
                : null,
        };

        const unchanged =
            normalized.name === original.name &&
            normalized.websiteUrl ===
            original.websiteUrl &&
            normalized.globalMerchantId ===
            original.globalMerchantId;

        if (unchanged) {
            setErrors({
                form: "No changes to save.",
            });
            return;
        }

        onSubmit(normalized);
    };

    return (
        <section className="merchant-form-card">
            <div className="merchant-form-heading">
                <div>
                    <p className="merchant-form-eyebrow">
                        Merchant details
                    </p>

                    <h2>Edit merchant</h2>

                    <p>
                        Keep your saved merchant information
                        accurate.
                    </p>
                </div>
            </div>

            {errors.form && (
                <div className="merchant-inline-error">
                    {errors.form}
                </div>
            )}

            <form
                className="merchant-form"
                onSubmit={handleSubmit}
                noValidate
            >
                <div className="merchant-form-grid">
                    <div className="merchant-form-group">
                        <label htmlFor="edit-merchant-name">
                            Merchant name
                        </label>

                        <input
                            id="edit-merchant-name"
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
                        <label htmlFor="edit-merchant-website">
                            Website
                        </label>

                        <input
                            id="edit-merchant-website"
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
                            disabled={submitting}
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
                        <label htmlFor="edit-merchant-global">
                            LinkVault identity
                            <span className="merchant-label-optional">
                                Optional
                            </span>
                        </label>

                        <select
                            id="edit-merchant-global"
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
                                No global merchant
                            </option>

                            {globalMerchants.map(
                                (globalMerchant) => (
                                    <option
                                        key={globalMerchant.id}
                                        value={
                                            globalMerchant.id
                                        }
                                    >
                                        {
                                            globalMerchant.name
                                        }
                                    </option>
                                )
                            )}
                        </select>
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
                            ? "Updating..."
                            : "Update Merchant"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default EditMerchantForm;