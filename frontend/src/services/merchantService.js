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