import { ArrowRight, LayoutDashboard, TrendingUp } from "lucide-react";
import { useNavigate } from "react-router-dom"
import Logo from "../components/layout/Logo";
import Button from "../components/ui/Button";

const FEATURES = [
    {
        icon: LayoutDashboard,
        title: "One source of truth",
        desc: "Track every application in a single table. Company, role, status, notes — all in one glance.",
        accent: "bg-indigo-50 text-indigo-600"
    },
    {
        icon: TrendingUp,
        title: "Visualise your pipeline",
        desc: "See exactly where you stand — how many interviews, offers, and rejections at a glance.",
        accent: "bg-emerald-50 text-emerald-600"
    }
]

export default function HomePage() {
    const navigate = useNavigate();

    return (
        <div>
            {/* Header */}
            <header className="sticky top-0 z-50 bg-white/90 backdrop-blur-sm border-b border-border">
                <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
                    <Logo />

                    <nav className="hidden md:flex items-center gap-8">
                        <a
                            href="#features"
                            className="text-sm text-slate-500 hover:text-slate-900 transition-colors"
                        >
                            Features
                        </a>
                    </nav>

                    <div className="flex items-center gap-3">
                        <Button
                            onClick={() => navigate("/login")}
                            variant="ghost"
                            size="sm"
                        >
                            Log in
                        </Button>

                        <Button
                            onClick={() => navigate("/signup")}
                            variant="primary"
                            size="sm"
                            className="font-medium"
                        >
                            Get started
                        </Button>
                    </div>
                </div>
            </header>

            <main>
                {/* Hero */}
                <section className="max-w-6xl mx-auto px-6 pt-20 pb-24">
                    <div className="max-w-3xl">
                        <h1 className="text-5xl md:text-6xl font-extrabold text-slate-900 leading-[1.08] tracking-tight mb-6">
                            Your job search,
                            <br />
                            <span className="text-slate-400">finally organized.</span>
                        </h1>

                        <p className="text-lg text-slate-500 leading-relaxed max-w-xl mb-10">
                            Jobtastic keeps every application in one clean workspace — track status, deadlines, and notes so nothing slips through the cracks.
                        </p>

                        <div className="flex flex-wrap gap-3 mb-14">
                            <Button
                                onClick={() => navigate("/signup")}
                                variant="primary"
                                size="md"
                                className="gap-2"
                            >
                                Start for free
                                <ArrowRight size={16} />
                            </Button>

                            <Button
                                onClick={() => navigate("/login")}
                                variant="outline"
                                size="md"
                                className="gap-2"
                            >
                                Log in
                            </Button>
                        </div>

                        <p className="text-sm text-slate-400">
                            No credit card required
                        </p>
                    </div>
                </section>

                {/* Features */}
                <section id="features" className="bg-slate-50 border-t border-border">
                    <div className="max-w-6xl mx-auto px-6 py-20">
                        <h2 className="text-2xl font-bold text-slate-900 mb-2 text-center">
                            Everything your job search needs
                        </h2>
                        <p className="text-slate-500 text-center mb-12">
                            Built for the modern job seeker — structured, calm and, clear.
                        </p>

                        <div className="grid md:grid-cols-2 gap-6">
                            {FEATURES.map(({ icon: Icon, title, desc, accent }) => (
                                <div
                                    key={title}
                                    className="bg-white rounded-2xl p-6 border border-border shadow-sm hover:shadow-md transition-shadow"
                                >
                                    <div
                                        className={`w-10 h-10 ${accent} rounded-xl flex items-center justify-center mb-4`}
                                    >
                                        <Icon size={20} />
                                    </div>
                                    <h3 className="font-semibold text-slate-900 mb-2">{title}</h3>
                                    <p className="text-sm text-slate-500 leading-relaxed">{desc}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            </main>

            {/* Footer */}
            <footer className="border-t border-border">
                <div className="max-w-6xl mx-auto px-6 py-8 flex flex-col md:flex-row items-center justify-between gap-4">
                    <div
                        onClick={() => navigate("/")}
                        className="flex items-center gap-2 hover:cursor-pointer"
                    >
                        <div className="w-5 h-5 bg-slate-900 rounded flex items-center justify-center">
                            <span className="text-white text-[9px] font-bold">J</span>
                        </div>
                        <span className="text-sm font-semibold text-slate-700">
                            Jobtastic
                        </span>
                    </div>
                    <p className="text-xs text-slate-400">
                        © 2026 Jobtastic. All rights reserved.
                    </p>
                </div>
            </footer>
        </div>
    )
}