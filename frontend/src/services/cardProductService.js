import { apiRequest } from "./api";

export async function getCardProducts({
    bankId,
    name = "",
    page = 0,
    size = 50,
}) {
    const params = new URLSearchParams({
        bankId: String(bankId),
        page: String(page),
        size: String(size),
    });

    if (name.trim()) {
        params.set("name", name.trim());
    }

    return apiRequest(`/card-products?${params.toString()}`);
}

export async function getCardProduct(id) {
    return apiRequest(`/card-products/${id}`);
}