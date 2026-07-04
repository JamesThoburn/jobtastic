import { createContext } from 'react';
import type { User } from './AuthProvider';

interface AuthContextType {
    user: User | null;
    loading: boolean;
    login: () => Promise<void>;
    logout: () => Promise<void>;
    updateUser: (updatedUser: Partial<User>) => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);