import { useState } from "react";

function AddLinkForm({ onLinkAdded, onCancel }) {

    const [title, setTitle] = useState("");
    const [url, setUrl] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        console.log("Adding link:", {
            title,
            url
        });

        if (!title.trim()) {
            alert("Title is required");
            return;
        }

        if (!url.trim()) {
            alert("URL is required");
            return;
        }

        try {

            const response = await fetch("http://localhost:8080/api/links", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    title,
                    url
                })
            });

            if (!response.ok) {
                throw new Error("Failed to create link");
            }

            const data = await response.json();

            console.log("Link created:", data);

            setTitle("");
            setUrl("");

            onLinkAdded();

        } catch (error) {

            console.error("Error creating link:", error);

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