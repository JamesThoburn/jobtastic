import axios from "axios";
import { CheckCircle2, Clock, LayoutDashboard, TrendingUp } from "lucide-react";
import { useEffect, useState } from "react";
import { Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";

type StatusBreakdownEntry = {
    status: string;
    count: number;
};

type AnalyticsSummary = {
    totalApplications: number;
    activeApplications: number;
    responseRate: number;
    offerRate: number;
    statusBreakdown: StatusBreakdownEntry[];
};

const STATUS_META: Record<string, { label: string; fill: string }> = {
    APPLIED: { label: "Applied", fill: "#3B82F6" },
    INTERVIEWING: { label: "Interviewing", fill: "#F59E0B" },
    OFFER: { label: "Offer", fill: "#10B981" },
    REJECTED: { label: "Rejected", fill: "#F43F5E" },
};

export default function AnalyticsPage() {
    const [summary, setSummary] = useState<AnalyticsSummary | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchSummary = async () => {
            try {
                const response = await axios.get<AnalyticsSummary>("/api/v1/analytics/summary", {
                    withCredentials: true,
                });
                setSummary(response.data);
            } catch (err) {
                console.error("Failed to load analytics", err);
                setError("Unable to load analytics right now.");
            } finally {
                setLoading(false);
            }
        };

        fetchSummary();
    }, []);

    const chartData = (summary?.statusBreakdown ?? []).map((entry) => {
        const meta = STATUS_META[entry.status.toUpperCase()] ?? {
            label: entry.status,
            fill: "#64748b",
        };

        return {
            name: meta.label,
            value: entry.count,
            fill: meta.fill,
        };
    });

    const kpis = [
        {
            label: "Applications",
            value: summary?.totalApplications ?? 0,
            sub: "all time",
            icon: LayoutDashboard,
            iconBg: "bg-slate-100",
            iconColor: "text-slate-600",
            valueCls: "text-slate-900",
        },
        {
            label: "Active",
            value: summary?.activeApplications ?? 0,
            sub: "in progress",
            icon: Clock,
            iconBg: "bg-blue-50",
            iconColor: "text-blue-600",
            valueCls: "text-blue-700",
        },
        {
            label: "Response rate",
            value: `${Math.round((summary?.responseRate ?? 0) * 100)}%`,
            sub: "apps that got a reply",
            icon: TrendingUp,
            iconBg: "bg-amber-50",
            iconColor: "text-amber-600",
            valueCls: "text-amber-700",
        },
        {
            label: "Offer rate",
            value: `${Math.round((summary?.offerRate ?? 0) * 100)}%`,
            sub: "of total applications",
            icon: CheckCircle2,
            iconBg: "bg-emerald-50",
            iconColor: "text-emerald-600",
            valueCls: "text-emerald-700",
        },
    ];

    return (
        <div className="overflow-auto">
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Analytics</h1>
                <p className="text-sm text-slate-500 mt-1">
                    Job search performance — all time
                </p>
            </div>

            {error && (
                <div className="mb-4 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">
                    {error}
                </div>
            )}

            <div className="grid grid-cols-2 xl:grid-cols-4 gap-3 mb-6">
                {kpis.map(({ label, value, sub, icon: Icon, iconBg, iconColor, valueCls }) => (
                    <div key={label} className="bg-white rounded-xl border border-border shadow-sm p-4">
                        <div className="flex items-center justify-between mb-2.5">
                            <span className="text-xs font-medium text-slate-500">{label}</span>
                            <div className={`w-7 h-7 ${iconBg} ${iconColor} rounded-lg flex items-center justify-center`}>
                                <Icon size={14} />
                            </div>
                        </div>
                        <p className={`text-2xl font-bold ${valueCls}`}>{loading ? "—" : value}</p>
                        <p className="text-xs text-slate-400 mt-0.5">{sub}</p>
                    </div>
                ))}
            </div>

            <div className="grid gap-4 mb-4">
                <div className="bg-white rounded-xl border border-border shadow-sm p-5">
                    <div className="mb-4">
                        <h2 className="text-sm font-semibold text-slate-900">Status breakdown</h2>
                        <p className="text-xs text-slate-400 mt-0.5">Current pipeline distribution</p>
                    </div>
                    {loading ? (
                        <div className="text-sm text-slate-500">Loading analytics…</div>
                    ) : (
                        <>
                            <ResponsiveContainer width="100%" height={160}>
                                <PieChart>
                                    <Pie
                                        data={chartData}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={48}
                                        outerRadius={72}
                                        paddingAngle={3}
                                        dataKey="value"
                                        strokeWidth={0}
                                    />
                                    <Tooltip
                                        contentStyle={{
                                            fontSize: 12,
                                            borderRadius: 8,
                                            border: "1px solid #e2e8f0",
                                            fontFamily: "'Plus Jakarta Sans', sans-serif",
                                        }}
                                    />
                                </PieChart>
                            </ResponsiveContainer>
                            <div className="flex flex-col gap-1.5 mt-2">
                                {chartData.map(({ name, fill }) => (
                                    <div key={name} className="flex items-center justify-between">
                                        <div className="flex items-center gap-2">
                                            <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: fill }} />
                                            <span className="text-xs text-slate-600">{name}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
