import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function ProtectedRoute() {
    const { user, loading } = useAuth();

    if (loading) {
        return <div>Loading...</div>;
    }

    // If there is no user in the context, redirect to login
    if (!user) {
        console.warn("ProtectedRoute: Redirecting to login (replacing history)!")
        return <Navigate to="/login" replace />;
    }

    // If authenticated, render the child routes
    return <Outlet />;
}