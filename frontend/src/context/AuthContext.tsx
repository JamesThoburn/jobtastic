import { createContext, useState, useContext, useEffect, useMemo } from 'react';
import apiService from '../services/apiService';

const AuthContext = createContext<any>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await apiService.get("/auth/me");
                setUser(response.data);
            } catch (err: any) {
                if (err.response?.status === 401) {
                    setUser(null);
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
        } catch (err) {
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

export const useAuth = () => useContext(AuthContext);