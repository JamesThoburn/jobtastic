import { useEffect, useMemo, useState } from "react";
import { AuthContext } from "./AuthContext";
import apiService from "../services/apiService";

export interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    createdAt: string;
}

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await apiService.get("/auth/me");
                setUser(response.data);
            } catch (err: unknown) {
                if (err && typeof err === 'object' && 'response' in err) {
                    const axiosError = err as { response: { status: number } };
                    if (axiosError.response?.status === 401) {
                        setUser(null);
                    }
                }
            } finally {
                setLoading(false);
            }
        };
        checkAuth();
    }, []);

    const login = async () => {
        try {
            const response = await apiService.get("/auth/me")
            setUser(response.data);
        } catch {
            setUser(null);
        }
    };

    const logout = async () => {
        try {
            await apiService.post("/auth/logout");
        } finally {
            setUser(null);
        }
    };

    const value = useMemo(() => ({ user, login, logout, loading }), [user, loading]);

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};