import { useState } from "react";
import { updateLink } from "../services/linkService";

function EditLinkForm({ link, onLinkUpdated, onCancel, onNotification }) {

    const [title, setTitle] = useState(link.title);
    const [url, setUrl] = useState(link.url);

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!title.trim()) {
            onNotification({
                message: "Title is required",
                type: "error"
            });
            return;
        }

        if (!url.trim()) {
            onNotification({
                message: "URL is required",
                type: "error"
            });
            return;
        }

        try {

            const data = await updateLink(link.id, {
                title,
                url
            });

            console.log("Link updated:", data);
            onNotification({
                message: "Link updated successfully",
                type: "success"
            });

            onLinkUpdated();

        } catch (error) {

            console.error("Error updating link:", error);
            onNotification({
                message: "Failed to update link",
                type: "error"
            });

        }
    };

    return (
        <div className="add-link-form">

            <h2>Edit Link</h2>

            <form onSubmit={handleSubmit}>

                <div className="form-group">

                    <label htmlFor="edit-title">
                        Title
                    </label>

                    <input
                        id="edit-title"
                        type="text"
                        value={title}
                        onChange={(event) => setTitle(event.target.value)}
                        placeholder="Enter link title"
                    />

                </div>

                <div className="form-group">

                    <label htmlFor="edit-url">
                        URL
                    </label>

                    <input
                        id="edit-url"
                        type="url"
                        value={url}
                        onChange={(event) => setUrl(event.target.value)}
                        placeholder="https://example.com"
                    />

                </div>

                <div className="form-actions">

                    <button
                        type="button"
                        onClick={onCancel}
                    >
                        Cancel
                    </button>

                    <button type="submit">
                        Update Link
                    </button>

                </div>

            </form>

        </div>
    );
}

export default EditLinkForm;