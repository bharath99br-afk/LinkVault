import { apiRequest } from "./api";

export async function getMerchants({
    page = 0,
    size = 100,
} = {}) {
    const params = new URLSearchParams();

    params.append("page", page);
    params.append("size", size);

    return apiRequest(`/merchants?${params.toString()}`);
}

export async function createMerchant(merchant) {
    return apiRequest("/merchants", {
        method: "POST",
        body: JSON.stringify(merchant),
    });
}

export async function updateMerchant(id, merchant) {
    return apiRequest(`/merchants/${id}`, {
        method: "PUT",
        body: JSON.stringify(merchant),
    });
}

export async function deleteMerchant(id) {
    return apiRequest(`/merchants/${id}`, {
        method: "DELETE",
    });
}

export async function getGlobalMerchants({
    page = 0,
    size = 100,
} = {}) {
    const params = new URLSearchParams();

    params.append("page", page);
    params.append("size", size);

    return apiRequest(
        `/global-merchants?${params.toString()}`
    );
}