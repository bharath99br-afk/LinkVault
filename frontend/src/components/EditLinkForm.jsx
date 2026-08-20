import { useState } from "react";

function EditLinkForm({ link, onLinkUpdated, onCancel }) {

    const [title, setTitle] = useState(link.title);
    const [url, setUrl] = useState(link.url);

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!title.trim()) {
            alert("Title is required");
            return;
        }

        if (!url.trim()) {
            alert("URL is required");
            return;
        }

        try {

            const response = await fetch(
                `http://localhost:8080/api/links/${link.id}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        title,
                        url
                    })
                }
            );

            if (!response.ok) {
                throw new Error("Failed to update link");
            }

            const data = await response.json();

            console.log("Link updated:", data);

            onLinkUpdated();

        } catch (error) {

            console.error("Error updating link:", error);

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