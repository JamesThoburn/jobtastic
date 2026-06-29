import { useState, type SubmitEvent } from "react";
import Logo from "../components/layout/Logo";
import Button from "../components/ui/Button";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { authService } from "../services/authService";

export default function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth();

  const handleLogin = async (e: SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    try {
      console.log("Attempting login...");
      await authService.login({ email, password });
      console.log("Adding to AuthContext")
      await login();
      console.log("Login successful, navigating to dashboard...");
      navigate("/dashboard");
    } catch (err: unknown) {
      console.log("Login failed:", err)

      if (err instanceof Error) {
        setError(err.message)
      } else if (err && typeof err === 'object' && 'response' in err) {
        const errorWithResponse = err as { response: { data: { message: string } } };
        setError(errorWithResponse.response.data.message || "Login failed. Please try again.");
      } else {
        setError("An unexpected error occurred. Please try again.");
      };
    }
  }

  return (
    <div className="bg-slate-50 min-h-screen flex flex-col">
      <header className="sticky top-0 z-50 p-6">
        <div className="w-fit">
          <Logo />
        </div>
      </header>

      <main className="flex-1 flex flex-col gap-5 items-center justify-center p-4 sm:p-6">
        <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl p-8 shadow-sm">

          {/* Form Header */}
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
              Welcome back
            </h1>
            <p className="text-sm text-slate-500 mt-1">
              Sign in to your Jobtastic account
            </p>
          </div>

          {/* Login Form */}
          <form onSubmit={handleLogin} className="space-y-4">
            {/* Email Field */}
            <div className="flex flex-col gap-1.5">
              <label htmlFor="email" className="text-sm font-medium text-slate-700">
                Email address
              </label>
              <input
                id="email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                className="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 transition-colors"
              />
            </div>

            {/* Password Field */}
            <div className="flex flex-col gap-1.5">
              <div className="flex justify-between items-center">
                <label htmlFor="password" className="text-sm font-medium text-slate-700">
                  Password
                </label>
                {/* ADD LOGIC FOR RESETTING PASSWORD */}
                <button
                  type="button"
                  className="hover:cursor-pointer text-xs font-medium text-slate-600 hover:text-slate-900 transition-colors"
                >
                  Forgot password?
                </button>
              </div>
              <input
                id="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 transition-colors"
              />
            </div>

            {error && <div className="p-3 bg-red-100 text-red-700 rounded">{error}</div>}

            {/* Log in button */}
            <Button
              type="submit"
              variant="primary"
              size="md"
              className="w-full flex justify-center"
            >
              Log in
            </Button>
          </form>
        </div>
        <p className="text-sm text-slate-500 flex gap-1.5">
          No account yet?
          <button
            onClick={() => navigate("/signup")}
            className="text-slate-900 font-semibold hover:underline hover:cursor-pointer"
          >
            Sign up free
          </button>
        </p>
      </main>
    </div>
  )
}
