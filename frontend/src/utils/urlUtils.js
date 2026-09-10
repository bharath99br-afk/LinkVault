const PRODUCT_HOSTNAME_PATTERN =
    /^(?=.{1,253}$)(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}$/;

export function normalizeProductUrl(value) {
    const trimmedValue = value.trim();

    if (!trimmedValue) {
        return "";
    }

    if (/^https?:\/\//i.test(trimmedValue)) {
        return trimmedValue;
    }

    return `https://${trimmedValue}`;
}

export function validateProductUrl(value) {
    const normalizedUrl = normalizeProductUrl(value);

    if (!normalizedUrl) {
        return {
            valid: false,
            message: "Product URL is required.",
        };
    }

    try {
        const parsedUrl = new URL(normalizedUrl);

        if (!["http:", "https:"].includes(parsedUrl.protocol)) {
            return {
                valid: false,
                message: "Please enter a valid HTTP or HTTPS URL.",
            };
        }

        const hostname = parsedUrl.hostname.toLowerCase();

        if (!PRODUCT_HOSTNAME_PATTERN.test(hostname)) {
            return {
                valid: false,
                message:
                    "Please enter a valid website address, such as amazon.in.",
            };
        }

        return {
            valid: true,
            value: parsedUrl.toString(),
        };
    } catch {
        return {
            valid: false,
            message:
                "Please enter a valid product URL, such as https://amazon.in.",
        };
    }
}