import { apiRequest } from "./api";

export async function getOffers({
    title = "",
    page = 0,
    size = 10,
} = {}) {
    const params = new URLSearchParams();

    if (title.trim()) {
        params.append("title", title.trim());
    }

    params.append("page", page);
    params.append("size", size);

    return apiRequest(`/offers?${params.toString()}`);
}

export async function getOffer(id) {
    return apiRequest(`/offers/${id}`);
}

export async function createOffer(offer) {
    return apiRequest("/offers", {
        method: "POST",
        body: JSON.stringify(offer),
    });
}

export async function deleteOffer(id) {
    await apiRequest(`/offers/${id}`, {
        method: "DELETE",
    });

    return true;
}

export async function addOfferBankApplicability(
    offerId,
    bankId
) {
    return apiRequest(`/offers/${offerId}/banks`, {
        method: "POST",
        body: JSON.stringify({
            bankId,
        }),
    });
}

export async function addOfferCardApplicability(
    offerId,
    cardProductId
) {
    return apiRequest(`/offers/${offerId}/cards`, {
        method: "POST",
        body: JSON.stringify({
            cardProductId,
        }),
    });
}

export async function saveOffer(offerId) {
    return apiRequest(`/saved-offers/${offerId}`, {
        method: "POST",
    });
}

export async function getSavedOffers() {
    return apiRequest("/saved-offers");
}

export async function getSavedOffer(offerId) {
    return apiRequest(`/saved-offers/${offerId}`);
}

export async function unsaveOffer(offerId) {
    return apiRequest(`/saved-offers/${offerId}`, {
        method: "DELETE",
    });
}