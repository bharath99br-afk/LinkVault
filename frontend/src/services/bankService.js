import { apiRequest } from "./api";

export async function getBanks({
    name = "",
    page = 0,
    size = 100,
} = {}) {
    const params = new URLSearchParams();

    if (name.trim()) {
        params.append("name", name.trim());
    }

    params.append("page", page);
    params.append("size", size);

    return apiRequest(`/banks?${params.toString()}`);
}