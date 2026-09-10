import { useState } from "react";
import { updateLink } from "../services/linkService";
import {
    normalizeProductUrl,
    validateProductUrl,
} from "../utils/urlUtils";

function EditLinkForm({
    link,
    onLinkUpdated,
    onCancel,
    onNotification,
}) {
    const [title, setTitle] = useState(link.title || "");
    const [url, setUrl] = useState(link.url || "");

    const [titleError, setTitleError] = useState("");
    const [urlError, setUrlError] = useState("");
    const [formMessage, setFormMessage] = useState("");
    const [saving, setSaving] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        setTitleError("");
        setUrlError("");
        setFormMessage("");

        const trimmedTitle = title.trim();

        if (!trimmedTitle) {
            setTitleError("Product title is required.");
            return;
        }

        const urlValidation = validateProductUrl(url);

        if (!urlValidation.valid) {
            setUrlError(urlValidation.message);
            return;
        }

        const originalTitle = (link.title || "").trim();
        const originalUrl = normalizeProductUrl(link.url || "");

        const titleChanged = trimmedTitle !== originalTitle;
        const urlChanged = urlValidation.value !== originalUrl;

        if (!titleChanged && !urlChanged) {
            setFormMessage("No changes to save.");
            return;
        }

        setSaving(true);

        try {
            const data = await updateLink(link.id, {
                title: trimmedTitle,
                url: urlValidation.value,
            });

            console.log("Link updated:", data);

            onNotification({
                message: "Product updated successfully",
                type: "success",
            });

            onLinkUpdated();
        } catch (error) {
            console.error("Error updating link:", error);

            setFormMessage(
                error.data?.message ||
                "Failed to update the product. Please try again."
            );
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="add-link-form">
            <h2>Edit Product</h2>

            <form onSubmit={handleSubmit} noValidate>
                <div className="form-group">
                    <label htmlFor="edit-title">
                        Product Title
                    </label>

                    <input
                        id="edit-title"
                        type="text"
                        value={title}
                        onChange={(event) => {
                            setTitle(event.target.value);

                            if (titleError) {
                                setTitleError("");
                            }

                            if (formMessage) {
                                setFormMessage("");
                            }
                        }}
                        placeholder="e.g. Sony WH-1000XM6"
                        aria-invalid={Boolean(titleError)}
                        aria-describedby={
                            titleError ? "edit-title-error" : undefined
                        }
                    />

                    {titleError && (
                        <p
                            id="edit-title-error"
                            className="form-field-error"
                        >
                            {titleError}
                        </p>
                    )}
                </div>

                <div className="form-group">
                    <label htmlFor="edit-url">
                        Product URL
                    </label>

                    <input
                        id="edit-url"
                        type="text"
                        value={url}
                        onChange={(event) => {
                            setUrl(event.target.value);

                            if (urlError) {
                                setUrlError("");
                            }

                            if (formMessage) {
                                setFormMessage("");
                            }
                        }}
                        placeholder="https://amazon.in/..."
                        aria-invalid={Boolean(urlError)}
                        aria-describedby={
                            urlError ? "edit-url-error" : undefined
                        }
                    />

                    {urlError && (
                        <p
                            id="edit-url-error"
                            className="form-field-error"
                        >
                            {urlError}
                        </p>
                    )}
                </div>

                {formMessage && (
                    <p className="form-message form-message-error">
                        {formMessage}
                    </p>
                )}

                <div className="form-actions">
                    <button
                        type="button"
                        onClick={onCancel}
                        disabled={saving}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        disabled={saving}
                    >
                        {saving ? "Updating..." : "Update Product"}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default EditLinkForm;