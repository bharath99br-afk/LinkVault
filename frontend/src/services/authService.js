import { apiRequest } from "./api";

const TOKEN_KEY = "linkvault_token";

export async function login(email, password) {
    const response = await apiRequest("/auth/login", {
        method: "POST",
        body: JSON.stringify({
            email,
            password,
        }),
    });

    const token = response.data.token;

    localStorage.setItem(TOKEN_KEY, token);

    return response.data;
}

export async function register(name, email, password) {
    return apiRequest("/auth/register", {
        method: "POST",
        body: JSON.stringify({
            name,
            email,
            password,
        }),
    });
}

export async function getCurrentUser() {
    return apiRequest("/users/me");
}

export function logout() {
    localStorage.removeItem(TOKEN_KEY);
}

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}