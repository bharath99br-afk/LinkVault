import { apiRequest } from "./api";

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

    return apiRequest(`/links?${params.toString()}`);
}

export async function createLink(link) {
    return apiRequest("/links", {
        method: "POST",
        body: JSON.stringify(link),
    });
}

export async function updateLink(id, link) {
    return apiRequest(`/links/${id}`, {
        method: "PUT",
        body: JSON.stringify(link),
    });
}

export async function deleteLink(id) {
    await apiRequest(`/links/${id}`, {
        method: "DELETE",
    });

    return true;
}