import { apiRequest } from "./api";

export async function findBestDeal({
    linkId = null,
    productId = null,
    merchantId = null,
    transactionAmount,
}) {
    const payload = {
        transactionAmount,
    };

    if (linkId !== null && linkId !== undefined) {
        payload.linkId = linkId;
    }

    if (productId !== null && productId !== undefined) {
        payload.productId = productId;
    }

    if (merchantId !== null && merchantId !== undefined) {
        payload.merchantId = merchantId;
    }

    return apiRequest("/deals/best", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}
