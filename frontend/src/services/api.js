const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export async function apiRequest(endpoint, options = {}) {
    const token = localStorage.getItem("linkvault_token");

    const headers = {
        "Content-Type": "application/json",
        ...options.headers,
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    let data = null;

    try {
        data = await response.json();
    } catch {
        // Response has no JSON body.
    }

    if (!response.ok) {
        const error = new Error(
            data?.message || "Something went wrong"
        );

        error.status = response.status;
        error.data = data;

        throw error;
    }

    return data;
}