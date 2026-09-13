export function normalizeProductUrl(value) {
    const trimmed = value.trim();

    if (!trimmed) {
        return "";
    }

    if (/^https?:\/\//i.test(trimmed)) {
        return trimmed;
    }

    return `https://${trimmed}`;
}

export function isValidProductUrl(value) {
    const normalized = normalizeProductUrl(value);

    if (!normalized) {
        return false;
    }

    try {
        const url = new URL(normalized);

        if (!["http:", "https:"].includes(url.protocol)) {
            return false;
        }

        const hostname = url.hostname.toLowerCase();

        // Require a realistic external hostname.
        // This prevents values such as "https://agsvsav".
        return (
            hostname === "localhost" ||
            hostname.includes(".")
        );
    } catch {
        return false;
    }
}

export function getProductDomain(value) {
    try {
        const normalized = normalizeProductUrl(value);
        const url = new URL(normalized);

        return url.hostname
            .replace(/^www\./i, "")
            .toLowerCase();
    } catch {
        return "";
    }
}