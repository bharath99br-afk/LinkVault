const API_BASE_URL = "http://localhost:8080/api/links";

export async function getLinks({
    title = "",
    page = 0,
    size = 5,
    sort = ""
} = {}) {

    const params = new URLSearchParams();

    if (title) {
        params.append("title", title);
    }

    params.append("page", page);
    params.append("size", size);

    if (sort) {
        params.append("sort", sort);
    }

    const response = await fetch(
        `${API_BASE_URL}?${params.toString()}`
    );

    if (!response.ok) {
        throw new Error("Failed to fetch links");
    }

    return response.json();
}


export async function createLink(link) {

    const response = await fetch(
        API_BASE_URL,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(link)
        }
    );

    if (!response.ok) {
        throw new Error("Failed to create link");
    }

    return response.json();
}


export async function updateLink(id, link) {

    const response = await fetch(
        `${API_BASE_URL}/${id}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(link)
        }
    );

    if (!response.ok) {
        throw new Error("Failed to update link");
    }

    return response.json();
}


export async function deleteLink(id) {

    const response = await fetch(
        `${API_BASE_URL}/${id}`,
        {
            method: "DELETE"
        }
    );

    if (!response.ok) {
        throw new Error("Failed to delete link");
    }

    return true;
}