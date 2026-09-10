import { useState } from "react";
import { createLink } from "../services/linkService";
import { validateProductUrl } from "../utils/urlUtils";

function AddLinkForm({ onLinkAdded, onCancel, onNotification }) {
    const [title, setTitle] = useState("");
    const [url, setUrl] = useState("");

    const [titleError, setTitleError] = useState("");
    const [urlError, setUrlError] = useState("");
    const [saving, setSaving] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        setTitleError("");
        setUrlError("");

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

        setSaving(true);

        try {
            const data = await createLink({
                title: trimmedTitle,
                url: urlValidation.value,
            });

            console.log("Link created:", data);

            setTitle("");
            setUrl("");

            onNotification({
                message: "Product saved successfully",
                type: "success",
            });

            onLinkAdded();
        } catch (error) {
            console.error("Error creating link:", error);

            onNotification({
                message:
                    error.data?.message ||
                    "Failed to save the product",
                type: "error",
            });
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="add-link-form">
            <h2>Save New Product</h2>

            <form onSubmit={handleSubmit} noValidate>
                <div className="form-group">
                    <label htmlFor="title">
                        Product Title
                    </label>

                    <input
                        id="title"
                        type="text"
                        value={title}
                        onChange={(event) => {
                            setTitle(event.target.value);

                            if (titleError) {
                                setTitleError("");
                            }
                        }}
                        placeholder="e.g. Sony WH-1000XM6"
                        aria-invalid={Boolean(titleError)}
                        aria-describedby={
                            titleError ? "title-error" : undefined
                        }
                    />

                    {titleError && (
                        <p
                            id="title-error"
                            className="form-field-error"
                        >
                            {titleError}
                        </p>
                    )}
                </div>

                <div className="form-group">
                    <label htmlFor="url">
                        Product URL
                    </label>

                    <input
                        id="url"
                        type="text"
                        value={url}
                        onChange={(event) => {
                            setUrl(event.target.value);

                            if (urlError) {
                                setUrlError("");
                            }
                        }}
                        placeholder="https://amazon.in/..."
                        aria-invalid={Boolean(urlError)}
                        aria-describedby={
                            urlError ? "url-error" : undefined
                        }
                    />

                    {urlError && (
                        <p
                            id="url-error"
                            className="form-field-error"
                        >
                            {urlError}
                        </p>
                    )}
                </div>

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
                        {saving ? "Saving..." : "Save Product"}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default AddLinkForm;