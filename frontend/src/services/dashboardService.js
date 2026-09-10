import { apiRequest } from "./api";

export async function getDashboardSummary() {
    return apiRequest("/dashboard/summary");
}