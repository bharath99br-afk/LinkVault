export function getLoginErrorMessage(error) {
    if (!error) {
        return "Unable to sign in. Please try again.";
    }

    if (error.status === 401) {
        return "Invalid email or password.";
    }

    if (error.status === 403) {
        return "You don't have permission to sign in.";
    }

    if (error.status === 429) {
        const retryAfterSeconds = error.retryAfterSeconds;
        if (Number.isFinite(retryAfterSeconds) && retryAfterSeconds > 0) {
            const waitTime = retryAfterSeconds >= 60
                ? `${Math.ceil(retryAfterSeconds / 60)} minute${retryAfterSeconds >= 120 ? "s" : ""}`
                : `${retryAfterSeconds} second${retryAfterSeconds === 1 ? "" : "s"}`;
            return `Too many login attempts. Please try again in ${waitTime}.`;
        }

        return "Too many login attempts. Please try again later.";
    }

    if (!error.status) {
        return "Unable to connect to LinkVault. Please check your connection.";
    }

    return error.data?.message || "Unable to sign in. Please try again.";
}

export function getRegisterErrorMessage(error) {
    if (!error) {
        return "Unable to create your account.";
    }

    if (error.status === 409) {
        return "An account with this email already exists.";
    }

    if (error.status === 400) {
        return error.data?.message || "Please check your details.";
    }

    if (!error.status) {
        return "Unable to connect to LinkVault. Please check your connection.";
    }

    return error.data?.message || "Unable to create your account.";
}
