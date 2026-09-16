export function normalizeMerchantUrl(value) {
    const trimmed = value.trim();

    if (!trimmed) {
        return "";
    }

    if (
        !trimmed.startsWith("http://") &&
        !trimmed.startsWith("https://")
    ) {
        return `https://${trimmed}`;
    }

    return trimmed;
}

export function isValidMerchantUrl(value) {
    const normalized = normalizeMerchantUrl(value);

    if (!normalized) {
        return false;
    }

    try {
        const url = new URL(normalized);

        if (
            url.protocol !== "http:" &&
            url.protocol !== "https:"
        ) {
            return false;
        }

        const hostname = url.hostname.toLowerCase();

        if (
            !hostname ||
            hostname === "localhost" ||
            hostname.endsWith(".localhost") ||
            hostname.endsWith(".local")
        ) {
            return false;
        }

        /*
         * Merchant websites should use a domain name rather than
         * a raw IP address.
         */
        if (
            !hostname.includes(".") ||
            /^[0-9.]+$/.test(hostname)
        ) {
            return false;
        }

        return true;
    } catch {
        return false;
    }
}