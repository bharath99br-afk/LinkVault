import { useEffect, useState } from "react";

import {
    isValidProductUrl,
    normalizeProductUrl,
} from "../../utils/productUtils";

function EditProductForm({
    product,
    merchants,
    merchantsLoading,
    onSubmit,
    onCancel,
    submitting,
}) {
    const [form, setForm] = useState({
        name: "",
        description: "",
        imageUrl: "",
        category: "",
        websiteUrl: "",
        merchantId: "",
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (!product) {
            return;
        }

        setForm({
            name: product.name || "",
            description: product.description || "",
            imageUrl: product.imageUrl || "",
            category: product.category || "",
            websiteUrl: product.websiteUrl || "",
            merchantId: product.merchantId
                ? String(product.merchantId)
                : "",
        });

        setErrors({});
    }, [product]);

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
                "Please enter the product website URL.";
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

        const normalized = {
            name: form.name.trim(),
            description: form.description.trim() || null,
            imageUrl: form.imageUrl.trim() || null,
            category: form.category.trim() || null,
            websiteUrl: normalizeProductUrl(form.websiteUrl),
            merchantId: form.merchantId
                ? Number(form.merchantId)
                : null,
        };

        const original = {
            name: product.name || "",
            description: product.description || null,
            imageUrl: product.imageUrl || null,
            category: product.category || null,
            websiteUrl: normalizeProductUrl(
                product.websiteUrl || ""
            ),
            merchantId: product.merchantId || null,
        };

        const unchanged =
            normalized.name === original.name &&
            normalized.description === original.description &&
            normalized.imageUrl === original.imageUrl &&
            normalized.category === original.category &&
            normalized.websiteUrl === original.websiteUrl &&
            normalized.merchantId === original.merchantId;

        if (unchanged) {
            setErrors({
                form: "No changes to save.",
            });
            return;
        }

        onSubmit(normalized);
    };

    return (
        <section className="product-form-card">
            <div className="product-form-heading">
                <div>
                    <p className="product-form-eyebrow">
                        Product details
                    </p>

                    <h2>Edit product</h2>

                    <p>
                        Keep your saved product information
                        accurate.
                    </p>
                </div>
            </div>

            {errors.form && (
                <div className="product-inline-error">
                    {errors.form}
                </div>
            )}

            <form
                className="product-form"
                onSubmit={handleSubmit}
                noValidate
            >
                <div className="product-form-grid">
                    <div className="product-form-group product-form-full">
                        <label htmlFor="edit-product-name">
                            Product name
                        </label>

                        <input
                            id="edit-product-name"
                            type="text"
                            value={form.name}
                            onChange={(event) =>
                                updateField(
                                    "name",
                                    event.target.value
                                )
                            }
                            maxLength={150}
                            className={
                                errors.name
                                    ? "product-input-error"
                                    : ""
                            }
                        />

                        {errors.name && (
                            <span className="product-field-error">
                                {errors.name}
                            </span>
                        )}
                    </div>

                    <div className="product-form-group product-form-full">
                        <label htmlFor="edit-product-description">
                            Description
                        </label>

                        <textarea
                            id="edit-product-description"
                            value={form.description}
                            onChange={(event) =>
                                updateField(
                                    "description",
                                    event.target.value
                                )
                            }
                            maxLength={500}
                            rows={3}
                        />

                        {errors.description && (
                            <span className="product-field-error">
                                {errors.description}
                            </span>
                        )}
                    </div>

                    <div className="product-form-group">
                        <label htmlFor="edit-product-category">
                            Category
                        </label>

                        <input
                            id="edit-product-category"
                            type="text"
                            value={form.category}
                            onChange={(event) =>
                                updateField(
                                    "category",
                                    event.target.value
                                )
                            }
                            maxLength={100}
                        />

                        {errors.category && (
                            <span className="product-field-error">
                                {errors.category}
                            </span>
                        )}
                    </div>

                    <div className="product-form-group">
                        <label htmlFor="edit-product-merchant">
                            Merchant
                        </label>

                        <select
                            id="edit-product-merchant"
                            value={form.merchantId}
                            onChange={(event) =>
                                updateField(
                                    "merchantId",
                                    event.target.value
                                )
                            }
                            disabled={merchantsLoading}
                        >
                            <option value="">
                                {merchantsLoading
                                    ? "Loading merchants..."
                                    : "No merchant"}
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
                    </div>

                    <div className="product-form-group product-form-full">
                        <label htmlFor="edit-product-website">
                            Product website
                        </label>

                        <input
                            id="edit-product-website"
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
                        />

                        {errors.websiteUrl && (
                            <span className="product-field-error">
                                {errors.websiteUrl}
                            </span>
                        )}
                    </div>

                    <div className="product-form-group product-form-full">
                        <label htmlFor="edit-product-image">
                            Image URL
                            <span className="product-label-optional">
                                Optional
                            </span>
                        </label>

                        <input
                            id="edit-product-image"
                            type="text"
                            value={form.imageUrl}
                            onChange={(event) =>
                                updateField(
                                    "imageUrl",
                                    event.target.value
                                )
                            }
                        />
                    </div>
                </div>

                <div className="product-form-actions">
                    <button
                        type="button"
                        className="product-secondary-button"
                        onClick={onCancel}
                        disabled={submitting}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="product-primary-button"
                        disabled={submitting}
                    >
                        {submitting
                            ? "Updating..."
                            : "Update Product"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default EditProductForm;