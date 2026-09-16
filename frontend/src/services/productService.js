import { apiRequest } from "./api";

export async function getProducts({
    name = "",
    category = "",
    page = 0,
    size = 8,
    sort = "",
} = {}) {
    const params = new URLSearchParams();

    if (name) {
        params.append("name", name);
    }

    if (category) {
        params.append("category", category);
    }

    params.append("page", page);
    params.append("size", size);

    if (sort) {
        params.append("sort", sort);
    }

    return apiRequest(`/products?${params.toString()}`);
}

export async function getProduct(id) {
    return apiRequest(`/products/${id}`);
}

/**
 * Generates product metadata from the supplied product URL.
 *
 * This does not create or save a product.
 */
export async function previewProduct(url) {
    return apiRequest("/products/preview", {
        method: "POST",
        body: JSON.stringify({
            url,
        }),
    });
}

export async function createProduct(product) {
    return apiRequest("/products", {
        method: "POST",
        body: JSON.stringify(product),
    });
}

export async function updateProduct(id, product) {
    return apiRequest(`/products/${id}`, {
        method: "PUT",
        body: JSON.stringify(product),
    });
}

export async function deleteProduct(id) {
    await apiRequest(`/products/${id}`, {
        method: "DELETE",
    });

    return true;
}