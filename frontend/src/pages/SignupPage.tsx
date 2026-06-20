import { useState, type FormEvent, type SubmitEvent } from "react";
import { useNavigate } from "react-router-dom";
import Logo from "../components/layout/Logo";
import Button from "../components/ui/Button";

export default function SignupPage() {
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSignup = (e: SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    // Handle signup logic here
    console.log("Signing up with:", { firstName, lastName, email, password });
  };

  return (
    <div className="bg-slate-50 min-h-screen flex flex-col">
      <header className="sticky top-0 z-50 p-6">
        <div className="w-fit">
          <Logo />
        </div>
      </header>

      <main className="flex-1 flex items-center justify-center p-4 sm:p-6 -mt-16">
        <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl p-8 shadow-sm">

          {/* Form Header */}
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
              Create your account
            </h1>
            <p className="text-sm text-slate-500 mt-1">
              Start tracking your job search today
            </p>
          </div>

          {/* Signup Form */}
          <form onSubmit={handleSignup} className="space-y-4">
            {/* First and Last Name Fields */}
            <div className="flex gap-4">
              {/* First Name Field */}
              <div className="flex-1 flex flex-col gap-1.5">
                <label htmlFor="firstName" className="text-sm font-medium text-slate-700">
                  First name
                </label>
                <input
                  id="firstName"
                  type="text"
                  required
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  placeholder="John"
                  className="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 transition-colors"
                />
              </div>

              {/* Last Name Field */}
              <div className="flex-1 flex flex-col gap-1.5">
                <label htmlFor="lastName" className="text-sm font-medium text-slate-700">
                  Last name
                </label>
                <input
                  id="lastName"
                  type="text"
                  required
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  placeholder="Doe"
                  className="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 transition-colors"
                />
              </div>
            </div>

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
              <label htmlFor="password" className="text-sm font-medium text-slate-700">
                Password
              </label>
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

            {/* Sign up button */}
            <Button
              type="submit"
              variant="primary"
              size="md"
              className="w-full flex justify-center mt-2"
            >
              Sign up
            </Button>
          </form>

          {/* Footer Link to Login page */}
          <div className="mt-6 text-sm text-slate-500 flex justify-center gap-1.5">
            Already have an account?
            <button
              onClick={() => navigate("/login")}
              className="text-slate-900 font-semibold hover:underline hover:cursor-pointer"
            >
              Log in
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}