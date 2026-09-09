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