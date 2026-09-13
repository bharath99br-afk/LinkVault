import { useEffect, useState } from "react";

import {
    isValidProductUrl,
    normalizeProductUrl,
} from "../../utils/productUtils";

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

    useEffect(() => {
        setForm(INITIAL_FORM);
        setErrors({});
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

    return (
        <section className="product-form-card">
            <div className="product-form-heading">
                <div>
                    <p className="product-form-eyebrow">
                        Add to your vault
                    </p>
                    <h2>Save a product</h2>
                    <p>
                        Keep a product ready for smarter purchase
                        decisions later.
                    </p>
                </div>
            </div>

            <form
                className="product-form"
                onSubmit={handleSubmit}
                noValidate
            >
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
                        />

                        {errors.category && (
                            <span className="product-field-error">
                                {errors.category}
                            </span>
                        )}
                    </div>

                    <div className="product-form-group">
                        <label htmlFor="product-merchant">
                            Merchant
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
                            placeholder="www.example.com/product"
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
                        <label htmlFor="product-image">
                            Image URL
                            <span className="product-label-optional">
                                Optional
                            </span>
                        </label>

                        <input
                            id="product-image"
                            type="text"
                            value={form.imageUrl}
                            onChange={(event) =>
                                updateField(
                                    "imageUrl",
                                    event.target.value
                                )
                            }
                            placeholder="https://example.com/product-image.jpg"
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
                            ? "Saving..."
                            : "Save Product"}
                    </button>
                </div>
            </form>
        </section>
    );
}

export default AddProductForm;