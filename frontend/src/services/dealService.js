import { apiRequest } from "./api";

export async function findBestDeal({
    linkId = null,
    transactionAmount,
}) {
    const payload = {
        transactionAmount,
    };

    if (linkId !== null && linkId !== undefined) {
        payload.linkId = linkId;
    }

    return apiRequest("/deals/best", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}