import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

import { useAuth } from "../hooks/useAuth";
import { getLoginErrorMessage } from "../services/authErrors";

function Login() {
    const { login } = useAuth();

    const navigate = useNavigate();
    const location = useLocation();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [emailError, setEmailError] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [serverError, setServerError] = useState("");

    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);

    const from = location.state?.from?.pathname || "/";

    const validate = () => {
        let valid = true;

        setEmailError("");
        setPasswordError("");
        setServerError("");

        const trimmedEmail = email.trim();

        if (!trimmedEmail) {
            setEmailError("Please enter your email address.");
            valid = false;
        } else if (
            !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)
        ) {
            setEmailError("Please enter a valid email address.");
            valid = false;
        }

        if (!password) {
            setPasswordError("Please enter your password.");
            valid = false;
        }

        return valid;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!validate()) {
            return;
        }

        setLoading(true);

        try {
            await login(email.trim(), password);
            navigate(from, { replace: true });
        } catch (error) {
            setServerError(getLoginErrorMessage(error));
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
                        Shop smarter.
                        <br />
                        <span>Keep more.</span>
                    </h1>

                    <p className="brand-description">
                        LinkVault helps you find the smartest way
                        to pay using the cards and offers you
                        already have.
                    </p>

                    <div className="value-points">
                        <div>
                            <span>✓</span>
                            Compare your payment options
                        </div>

                        <div>
                            <span>✓</span>
                            Find applicable offers
                        </div>

                        <div>
                            <span>✓</span>
                            See exactly how much you save
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
                                Welcome back
                            </h2>

                            <p>
                                Your next saving could be one
                                checkout away.
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

                        <form onSubmit={handleSubmit} noValidate>

                            <div className="form-group">
                                <label htmlFor="login-email">
                                    Email address
                                </label>

                                <input
                                    id="login-email"
                                    type="email"
                                    autoComplete="email"
                                    value={email}
                                    onChange={(event) => {
                                        setEmail(event.target.value);
                                        setEmailError("");
                                        setServerError("");
                                    }}
                                    className={
                                        emailError
                                            ? "input-error"
                                            : ""
                                    }
                                    placeholder="you@example.com"
                                />

                                {emailError && (
                                    <span className="field-error">
                                        {emailError}
                                    </span>
                                )}
                            </div>

                            <div className="form-group">
                                <div className="password-label-row">
                                    <label htmlFor="login-password">
                                        Password
                                    </label>
                                </div>

                                <div className="password-input-wrapper">
                                    <input
                                        id="login-password"
                                        type={
                                            showPassword
                                                ? "text"
                                                : "password"
                                        }
                                        autoComplete="current-password"
                                        value={password}
                                        onChange={(event) => {
                                            setPassword(
                                                event.target.value
                                            );
                                            setPasswordError("");
                                            setServerError("");
                                        }}
                                        className={
                                            passwordError
                                                ? "input-error"
                                                : ""
                                        }
                                        placeholder="Enter your password"
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

                                {passwordError && (
                                    <span className="field-error">
                                        {passwordError}
                                    </span>
                                )}
                            </div>

                            <button
                                type="submit"
                                className="auth-submit"
                                disabled={loading}
                            >
                                {loading
                                    ? "Signing you in..."
                                    : "Sign in to LinkVault"}
                            </button>
                        </form>

                        <p className="auth-footer">
                            New to LinkVault?{" "}
                            <Link to="/register">
                                Create your free account
                            </Link>
                        </p>

                    </div>
                </section>

            </div>
        </div>
    );
}

export default Login;