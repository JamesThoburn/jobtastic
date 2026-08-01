import { useNavigate, useSearchParams } from "react-router-dom";
import Logo from "../components/layout/Logo";
import { useState, type SubmitEvent } from "react";
import { authService } from "../services/authService";
import Button from "../components/ui/Button";

export default function ResetPasswordPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e: SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError("");

        if (newPassword !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }
        if (!token) {
            setError("Missing or invalid reset link");
            return;
        }

        try {
            await authService.resetPassword({ token, newPassword });
            setSuccess(true);
            setTimeout(() => navigate("/login"), 2000);
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else if (err && typeof err === 'object' && 'response' in err) {
                const errorWithResponse = err as { response: { data: { message: string } } };
                setError(errorWithResponse.response.data.message || "Reset failed. Please try again.");
            } else {
                setError("An unexpected error occurred. Please try again.");
            }
        }
    }

    if (!token) {
        return (
            <div className="bg-slate-50 min-h-screen flex items-center justify-center p-4">
                <p className="text-sm text-slate-700">Invalid or missing reset token</p>
            </div>
        )
    }

    return (
        <div className="bg-slate-50 min-h-screen flex flex-col">
            <header className="sticky top-0 z-50 p-6">
                <div className="w-fit">
                    <Logo />
                </div>
            </header>

            <main className="flex-1 flex flex-col gap-5 items-center justify-center p-4 sm:p-6">
                <div className="w-full max-w-md bg-white border border-border rounded-2xl p-8 shadow-sm">
                    <div className="mb-6">
                        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
                            Choose a new password
                        </h1>
                    </div>

                    {success ? (
                        <div className="p-3 bg-green-100 text-green-700 rounded text-sm">
                            Password reset. Redirecting to login...
                        </div>
                    ) : (
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div className="flex flex-col gap-1.5">
                                <label htmlFor="newPassword" className="text-sm font-medium text-slate-700">
                                    New password
                                </label>
                                <input 
                                    id="newPassword"
                                    type="password"
                                    required
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                    placeholder="••••••••"
                                    className="w-full px-3 py-2.5 border border-border rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 transition-colors"
                                />
                            </div>

                            <div className="flex flex-col gap-1.5">
                                <label htmlFor="confirmPassword" className="text-sm font-medium text-slate-700">
                                    Confirm password
                                </label>
                                <input 
                                    id="confirmPassword"
                                    type="password"
                                    required
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    placeholder="••••••••"
                                    className="w-full px-3 py-2.5 border border-border rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 transition-colors"
                                />
                            </div>

                            {error && <div className="p-3 bg-red-100 text-red-700 rounded">{error}</div>}
                        
                            <Button type="submit" variant="primary" size="md" className="w-full flex justify-center">
                                Reset password
                            </Button>
                        </form>
                    )}
                </div>
            </main>
        </div>
    )
}
