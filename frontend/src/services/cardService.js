import { apiRequest } from "./api";

export async function getCards({
    name = "",
    page = 0,
    size = 6,
} = {}) {
    const params = new URLSearchParams();

    if (name.trim()) {
        params.append("name", name.trim());
    }

    params.append("page", page);
    params.append("size", size);

    return apiRequest(`/cards?${params.toString()}`);
}

export async function getCard(id) {
    return apiRequest(`/cards/${id}`);
}

export async function createCard(card) {
    return apiRequest("/cards", {
        method: "POST",
        body: JSON.stringify(card),
    });
}

export async function updateCard(id, card) {
    return apiRequest(`/cards/${id}`, {
        method: "PUT",
        body: JSON.stringify(card),
    });
}

export async function deleteCard(id) {
    await apiRequest(`/cards/${id}`, {
        method: "DELETE",
    });

    return true;
}