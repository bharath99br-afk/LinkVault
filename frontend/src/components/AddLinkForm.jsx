import { useState } from "react";
import { createLink } from "../services/linkService";

function AddLinkForm({ onLinkAdded, onCancel, onNotification }) {

    const [title, setTitle] = useState("");
    const [url, setUrl] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        console.log("Adding link:", {
            title,
            url
        });

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

            const data = await createLink({
                title,
                url
            });

            console.log("Link created:", data);

            setTitle("");
            setUrl("");

            onNotification({
                message: "Link added successfully",
                type: "success"
            });

            onLinkAdded();

        } catch (error) {

            console.error("Error creating link:", error);
            onNotification({
                message: "Failed to add link",
                type: "error"
            });

        }
    };

    return (
        <div className="add-link-form">

            <h2>Add New Link</h2>

            <form onSubmit={handleSubmit}>

                <div className="form-group">

                    <label htmlFor="title">
                        Title
                    </label>

                    <input
                        id="title"
                        type="text"
                        value={title}
                        onChange={(event) => setTitle(event.target.value)}
                        placeholder="Enter link title"
                    />

                </div>

                <div className="form-group">

                    <label htmlFor="url">
                        URL
                    </label>

                    <input
                        id="url"
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
                        Save Link
                    </button>

                </div>

            </form>

        </div>
    );
}

export default AddLinkForm;