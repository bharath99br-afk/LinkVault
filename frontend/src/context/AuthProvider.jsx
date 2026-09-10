import { useEffect, useState } from "react";

import {
    getCurrentUser,
    getToken,
    login as loginUser,
    logout as logoutUser,
    register as registerUser,
} from "../services/authService";

import AuthContext from "./AuthContext";

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const isAuthenticated = !!user;

    useEffect(() => {
        const initializeAuth = async () => {
            const token = getToken();

            if (!token) {
                setLoading(false);
                return;
            }

            try {
                const response = await getCurrentUser();
                setUser(response.data);
            } catch (error) {
                console.error(
                    "Authentication initialization failed:",
                    error
                );

                logoutUser();
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        initializeAuth();
    }, []);

    const login = async (email, password) => {
        await loginUser(email, password);

        const response = await getCurrentUser();
        setUser(response.data);
    };

    const register = async (name, email, password) => {
        return registerUser(name, email, password);
    };

    const logout = () => {
        logoutUser();
        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                isAuthenticated,
                login,
                register,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}