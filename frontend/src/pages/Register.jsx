import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useAuth } from "../hooks/useAuth";
import { getRegisterErrorMessage } from "../services/authErrors";

function Register() {
    const { register } = useAuth();
    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [errors, setErrors] = useState({});
    const [serverError, setServerError] = useState("");
    const [success, setSuccess] = useState("");

    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);

    const validate = () => {
        const nextErrors = {};

        if (!name.trim()) {
            nextErrors.name = "Please enter your name.";
        }

        const trimmedEmail = email.trim();

        if (!trimmedEmail) {
            nextErrors.email =
                "Please enter your email address.";
        } else if (
            !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)
        ) {
            nextErrors.email =
                "Please enter a valid email address.";
        }

        if (!password) {
            nextErrors.password =
                "Please create a password.";
        } else if (password.length < 6) {
            nextErrors.password =
                "Password must be at least 6 characters.";
        }

        setErrors(nextErrors);

        return Object.keys(nextErrors).length === 0;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setServerError("");
        setSuccess("");

        if (!validate()) {
            return;
        }

        setLoading(true);

        try {
            await register(
                name.trim(),
                email.trim(),
                password
            );

            setSuccess(
                "Account created successfully. Taking you to sign in..."
            );

            setTimeout(() => {
                navigate("/login");
            }, 1000);
        } catch (error) {
            setServerError(
                getRegisterErrorMessage(error)
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-layout">

                <section className="auth-brand-panel">
                    <div className="brand-mark">
                        LV
                    </div>

                    <p className="brand-name">
                        LinkVault
                    </p>

                    <h1>
                        Your money
                        <br />
                        <span>deserves better.</span>
                    </h1>

                    <p className="brand-description">
                        Save the products you want. Add the cards
                        you own. Let LinkVault help you discover
                        the better way to pay.
                    </p>

                    <div className="saving-highlight">
                        <span className="saving-icon">
                            ₹
                        </span>

                        <div>
                            <strong>
                                Every saving counts.
                            </strong>

                            <p>
                                Even a small saving at checkout
                                adds up over time.
                            </p>
                        </div>
                    </div>
                </section>

                <section className="auth-form-panel">
                    <div className="auth-card">

                        <div className="mobile-brand">
                            <div className="brand-mark">
                                LV
                            </div>

                            <strong>LinkVault</strong>
                        </div>

                        <div className="auth-heading">
                            <h2>
                                Start saving before you shop
                            </h2>

                            <p>
                                Create your free LinkVault account.
                            </p>
                        </div>

                        {serverError && (
                            <div
                                className="auth-message auth-message-error"
                                role="alert"
                            >
                                {serverError}
                            </div>
                        )}

                        {success && (
                            <div
                                className="auth-message auth-message-success"
                                role="status"
                            >
                                {success}
                            </div>
                        )}

                        <form onSubmit={handleSubmit} noValidate>

                            <div className="form-group">
                                <label htmlFor="register-name">
                                    Name
                                </label>

                                <input
                                    id="register-name"
                                    type="text"
                                    autoComplete="name"
                                    value={name}
                                    onChange={(event) => {
                                        setName(event.target.value);

                                        setErrors((current) => ({
                                            ...current,
                                            name: "",
                                        }));

                                        setServerError("");
                                    }}
                                    className={
                                        errors.name
                                            ? "input-error"
                                            : ""
                                    }
                                    placeholder="Your name"
                                />

                                {errors.name && (
                                    <span className="field-error">
                                        {errors.name}
                                    </span>
                                )}
                            </div>

                            <div className="form-group">
                                <label htmlFor="register-email">
                                    Email address
                                </label>

                                <input
                                    id="register-email"
                                    type="email"
                                    autoComplete="email"
                                    value={email}
                                    onChange={(event) => {
                                        setEmail(event.target.value);

                                        setErrors((current) => ({
                                            ...current,
                                            email: "",
                                        }));

                                        setServerError("");
                                    }}
                                    className={
                                        errors.email
                                            ? "input-error"
                                            : ""
                                    }
                                    placeholder="you@example.com"
                                />

                                {errors.email && (
                                    <span className="field-error">
                                        {errors.email}
                                    </span>
                                )}
                            </div>

                            <div className="form-group">
                                <label htmlFor="register-password">
                                    Password
                                </label>

                                <div className="password-input-wrapper">
                                    <input
                                        id="register-password"
                                        type={
                                            showPassword
                                                ? "text"
                                                : "password"
                                        }
                                        autoComplete="new-password"
                                        value={password}
                                        onChange={(event) => {
                                            setPassword(
                                                event.target.value
                                            );

                                            setErrors((current) => ({
                                                ...current,
                                                password: "",
                                            }));

                                            setServerError("");
                                        }}
                                        className={
                                            errors.password
                                                ? "input-error"
                                                : ""
                                        }
                                        placeholder="At least 6 characters"
                                    />

                                    <button
                                        type="button"
                                        className="password-toggle"
                                        onClick={() =>
                                            setShowPassword(
                                                (current) =>
                                                    !current
                                            )
                                        }
                                    >
                                        {showPassword
                                            ? "Hide"
                                            : "Show"}
                                    </button>
                                </div>

                                {errors.password && (
                                    <span className="field-error">
                                        {errors.password}
                                    </span>
                                )}
                            </div>

                            <button
                                type="submit"
                                className="auth-submit"
                                disabled={loading}
                            >
                                {loading
                                    ? "Creating your account..."
                                    : "Create my LinkVault account"}
                            </button>
                        </form>

                        <p className="auth-footer">
                            Already have an account?{" "}
                            <Link to="/login">
                                Sign in
                            </Link>
                        </p>

                    </div>
                </section>

            </div>
        </div>
    );
}

export default Register;