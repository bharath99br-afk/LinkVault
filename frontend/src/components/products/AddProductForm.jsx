import { useEffect, useState } from "react";

import {
    isValidProductUrl,
    normalizeProductUrl,
} from "../../utils/productUtils";

import { previewProduct } from "../../services/productService";

const INITIAL_FORM = {
    name: "",
    description: "",
    imageUrl: "",
    category: "",
    websiteUrl: "",
    merchantId: "",
};

function AddProductForm({
    merchants,
    merchantsLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [form, setForm] = useState(INITIAL_FORM);
    const [errors, setErrors] = useState({});

    const [sourceUrl, setSourceUrl] = useState("");

    const [preview, setPreview] = useState(null);
    const [previewLoading, setPreviewLoading] = useState(false);
    const [previewError, setPreviewError] = useState("");

    useEffect(() => {
        setForm(INITIAL_FORM);
        setErrors({});
        setSourceUrl("");
        setPreview(null);
        setPreviewError("");
    }, []);

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

    const validateSourceUrl = () => {
        const normalizedUrl = sourceUrl.trim();

        if (!normalizedUrl) {
            setPreviewError("Please enter a product URL.");
            return false;
        }

        if (!isValidProductUrl(normalizedUrl)) {
            setPreviewError("Please enter a valid product URL.");
            return false;
        }

        setPreviewError("");
        return true;
    };

    const handlePreview = async () => {
        if (!validateSourceUrl()) {
            return;
        }

        setPreviewLoading(true);
        setPreviewError("");
        setPreview(null);

        try {
            const normalizedUrl = normalizeProductUrl(
                sourceUrl.trim()
            );

            const response = await previewProduct(normalizedUrl);
            const data = response?.data;

            if (!data) {
                throw new Error(
                    "Product information could not be retrieved."
                );
            }

            setPreview(data);

            setForm((current) => ({
                ...current,
                name: data.name || "",
                description: data.description || "",
                imageUrl: data.imageUrl || "",
                category: data.category || "",
                websiteUrl:
                    data.websiteUrl || normalizedUrl,
            }));
        } catch (requestError) {
            console.error(
                "Failed to generate product preview:",
                requestError
            );

            setPreviewError(
                requestError?.message ||
                "Unable to generate a product preview."
            );
        } finally {
            setPreviewLoading(false);
        }
    };

    const validate = () => {
        const nextErrors = {};

        const name = form.name.trim();
        const description = form.description.trim();
        const category = form.category.trim();
        const websiteUrl = form.websiteUrl.trim();

        if (!name) {
            nextErrors.name = "Please enter a product name.";
        } else if (name.length < 2) {
            nextErrors.name =
                "Product name must be at least 2 characters.";
        } else if (name.length > 150) {
            nextErrors.name =
                "Product name cannot exceed 150 characters.";
        }

        if (description.length > 500) {
            nextErrors.description =
                "Description cannot exceed 500 characters.";
        }

        if (category.length > 100) {
            nextErrors.category =
                "Category cannot exceed 100 characters.";
        }

        if (!websiteUrl) {
            nextErrors.websiteUrl =
                "Product website URL is required.";
        } else if (!isValidProductUrl(websiteUrl)) {
            nextErrors.websiteUrl =
                "Please enter a valid website URL.";
        }

        setErrors(nextErrors);

        return Object.keys(nextErrors).length === 0;
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!validate()) {
            return;
        }

        onSubmit({
            name: form.name.trim(),
            description: form.description.trim() || null,
            imageUrl: form.imageUrl.trim() || null,
            category: form.category.trim() || null,
            websiteUrl: normalizeProductUrl(form.websiteUrl),
            merchantId: form.merchantId
                ? Number(form.merchantId)
                : null,
        });
    };

    const handleChangeUrl = (event) => {
        setSourceUrl(event.target.value);

        if (preview) {
            setPreview(null);
            setPreviewError("");
            setForm(INITIAL_FORM);
        }

        if (previewError) {
            setPreviewError("");
        }
    };

    return (
        <section className="product-form-card">
            <div className="product-form-heading">
                <div>
                    <p className="product-form-eyebrow">
                        Add to your vault
                    </p>

                    <h2>Save a product</h2>

                    <p>
                        Paste a product link and LinkVault will
                        identify the product for you.
                    </p>
                </div>
            </div>

            <form
                className="product-form"
                onSubmit={handleSubmit}
                noValidate
            >
                {/* -------------------------------------------------
                    PRODUCT URL / PREVIEW
                -------------------------------------------------- */}

                <div className="product-url-intelligence">
                    <div className="product-form-group product-form-full">
                        <label htmlFor="product-source-url">
                            Product URL
                        </label>

                        <div className="product-url-preview-row">
                            <input
                                id="product-source-url"
                                type="text"
                                value={sourceUrl}
                                onChange={handleChangeUrl}
                                placeholder="Paste a product link from a shopping website"
                                disabled={
                                    previewLoading || submitting
                                }
                                autoComplete="url"
                            />

                            <button
                                type="button"
                                className="product-preview-button"
                                onClick={handlePreview}
                                disabled={
                                    previewLoading ||
                                    submitting ||
                                    !sourceUrl.trim()
                                }
                            >
                                {previewLoading
                                    ? "Identifying..."
                                    : "Identify Product"}
                            </button>
                        </div>

                        <p className="product-url-help">
                            LinkVault will extract available product
                            details from the source page.
                        </p>

                        {previewError && (
                            <span className="product-field-error">
                                {previewError}
                            </span>
                        )}
                    </div>

                    {/* -------------------------------------------------
                        PRODUCT PREVIEW
                    -------------------------------------------------- */}

                    {preview && (
                        <div className="product-preview-card">
                            {/* -------------------------------------------------
            PREVIEW HEADER / STATUS
        -------------------------------------------------- */}
                            <div className="product-preview-header">
                                <div>
                                    <p className="product-preview-eyebrow">
                                        Product identified
                                    </p>
                                    <p className="product-preview-source">
                                        Details extracted from the source page
                                    </p>
                                </div>

                                <div className="product-preview-status">
                                    <span
                                        className="product-preview-check"
                                        aria-hidden="true"
                                    >
                                        ✓
                                    </span>

                                    <span>Ready to save</span>
                                </div>
                            </div>

                            {/* -------------------------------------------------
            PREVIEW CONTENT
        -------------------------------------------------- */}
                            <div className="product-preview-body">
                                <div className="product-preview-image">
                                    {preview.imageUrl ? (
                                        <img
                                            src={preview.imageUrl}
                                            alt={
                                                preview.name ||
                                                "Product preview"
                                            }
                                            onError={(event) => {
                                                event.currentTarget.style.display =
                                                    "none";
                                            }}
                                        />
                                    ) : (
                                        <div
                                            className="product-preview-image-placeholder"
                                            aria-hidden="true"
                                        >
                                            <span>⌁</span>
                                        </div>
                                    )}
                                </div>

                                <div className="product-preview-details">
                                    <h3>
                                        {preview.name ||
                                            "Unnamed product"}
                                    </h3>

                                    {preview.description && (
                                        <p className="product-preview-description">
                                            {preview.description}
                                        </p>
                                    )}

                                    <div className="product-preview-meta">
                                        {preview.merchantName && (
                                            <span>
                                                <strong>Merchant:</strong>{" "}
                                                {preview.merchantName}
                                            </span>
                                        )}

                                        {preview.category && (
                                            <span className="product-preview-category">
                                                {preview.category}
                                            </span>
                                        )}

                                        {preview.price != null && (
                                            <span className="product-preview-price">
                                                {preview.currency
                                                    ? `${preview.currency} `
                                                    : ""}
                                                {preview.price}
                                            </span>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* -------------------------------------------------
                    PRODUCT DETAILS
                -------------------------------------------------- */}

                {preview && (
                    <div className="product-form-grid">
                        <div className="product-form-group product-form-full">
                            <label htmlFor="product-name">
                                Product name
                            </label>

                            <input
                                id="product-name"
                                type="text"
                                value={form.name}
                                onChange={(event) =>
                                    updateField(
                                        "name",
                                        event.target.value
                                    )
                                }
                                placeholder="e.g. Sony WH-1000XM5"
                                maxLength={150}
                                className={
                                    errors.name
                                        ? "product-input-error"
                                        : ""
                                }
                                disabled={submitting}
                            />

                            {errors.name && (
                                <span className="product-field-error">
                                    {errors.name}
                                </span>
                            )}
                        </div>

                        <div className="product-form-group product-form-full">
                            <label htmlFor="product-description">
                                Description
                            </label>

                            <textarea
                                id="product-description"
                                value={form.description}
                                onChange={(event) =>
                                    updateField(
                                        "description",
                                        event.target.value
                                    )
                                }
                                placeholder="Optional notes about this product"
                                maxLength={500}
                                rows={3}
                                className={
                                    errors.description
                                        ? "product-input-error"
                                        : ""
                                }
                                disabled={submitting}
                            />

                            {errors.description && (
                                <span className="product-field-error">
                                    {errors.description}
                                </span>
                            )}
                        </div>

                        <div className="product-form-group">
                            <label htmlFor="product-category">
                                Category
                            </label>

                            <input
                                id="product-category"
                                type="text"
                                value={form.category}
                                onChange={(event) =>
                                    updateField(
                                        "category",
                                        event.target.value
                                    )
                                }
                                placeholder="e.g. Electronics"
                                maxLength={100}
                                className={
                                    errors.category
                                        ? "product-input-error"
                                        : ""
                                }
                                disabled={submitting}
                            />

                            {errors.category && (
                                <span className="product-field-error">
                                    {errors.category}
                                </span>
                            )}
                        </div>

                        <div className="product-form-group">
                            <label htmlFor="product-merchant">
                                Your merchant
                            </label>

                            <select
                                id="product-merchant"
                                value={form.merchantId}
                                onChange={(event) =>
                                    updateField(
                                        "merchantId",
                                        event.target.value
                                    )
                                }
                                disabled={
                                    merchantsLoading ||
                                    submitting
                                }
                            >
                                <option value="">
                                    {merchantsLoading
                                        ? "Loading merchants..."
                                        : "Select merchant"}
                                </option>

                                {merchants.map((merchant) => (
                                    <option
                                        key={merchant.id}
                                        value={merchant.id}
                                    >
                                        {merchant.name}
                                    </option>
                                ))}
                            </select>

                            {preview?.merchantName && (
                                <p className="product-merchant-detected">
                                    Detected:{" "}
                                    <strong>
                                        {preview.merchantName}
                                    </strong>
                                </p>
                            )}
                        </div>

                        <div className="product-form-group product-form-full">
                            <label htmlFor="product-website">
                                Product website
                            </label>

                            <input
                                id="product-website"
                                type="text"
                                value={form.websiteUrl}
                                onChange={(event) =>
                                    updateField(
                                        "websiteUrl",
                                        event.target.value
                                    )
                                }
                                className={
                                    errors.websiteUrl
                                        ? "product-input-error"
                                        : ""
                                }
                                disabled={submitting}
                            />

                            {errors.websiteUrl && (
                                <span className="product-field-error">
                                    {errors.websiteUrl}
                                </span>
                            )}
                        </div>
                    </div>
                )}

                {/* -------------------------------------------------
                    ACTIONS
                -------------------------------------------------- */}

                <div className="product-form-actions">
                    <button
                        type="button"
                        className="product-secondary-button"
                        onClick={onCancel}
                        disabled={
                            submitting || previewLoading
                        }
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="product-primary-button"
                        disabled={
                            submitting ||
                            previewLoading ||
                            !preview
                        }
                    >
                        {submitting
                            ? "Saving..."
                            : "Save Product"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default AddProductForm;