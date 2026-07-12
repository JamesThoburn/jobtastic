import { CheckCircle2, Clock, LayoutDashboard, TrendingUp } from "lucide-react"
import { Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";

const KPIs = [
    {
        label: "Applications",
        value: 8, // Placeholder value
        sub: "all time",
        icon: LayoutDashboard,
        iconBg: "bg-slate-100",
        iconColor: "text-slate-600",
        valueCls: "text-slate-900"
    },
    {
        label: "Active",
        value: 6, // Placeholder value
        sub: "in progress",
        icon: Clock,
        iconBg: "bg-blue-50",
        iconColor: "text-blue-600",
        valueCls: "text-blue-700"
    },
    {
        label: "Response rate",
        value: "50%", // Placeholder value
        sub: "apps that got a reply",
        icon: TrendingUp,
        iconBg: "bg-amber-50",
        iconColor: "text-amber-600",
        valueCls: "text-amber-700"
    },
    {
        label: "Offer rate",
        value: "13%", // Placeholder value
        sub: "of total applications",
        icon: CheckCircle2,
        iconBg: "bg-emerald-50",
        iconColor: "text-emerald-600",
        valueCls: "text-emerald-700"
    }
];

// Placeholder values in here
const STATUS_BREAKDOWN = [
  { name: "Applied",      value: 3, fill: "#3B82F6" },
  { name: "Interviewing", value: 3, fill: "#F59E0B" },
  { name: "Offer",        value: 1, fill: "#10B981" },
  { name: "Rejected",     value: 1, fill: "#F43F5E" },
];

export default function AnalyticsPage() {
    return (
        <div className="overflow-auto">
            {/* Header */}
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Analytics</h1>
                <p className="text-sm text-slate-500 mt-1">
                    Job search performance — all time
                </p>
            </div>

            {/* KPI Cards */}
            <div className="grid grid-cols-2 xl:grid-cols-4 gap-3 mb-6">
                {KPIs.map(({ label, value, sub, icon: Icon, iconBg, iconColor, valueCls }) => (
                    <div key={label} className="bg-white rounded-xl border border-border shadow-sm p-4">
                        <div className="flex items-center justify-between mb-2.5">
                            <span className="text-xs font-medium text-slate-500">{label}</span>
                            <div className={`w-7 h-7 ${iconBg} ${iconColor} rounded-lg flex items-center justify-center`}>
                                <Icon size={14} />
                            </div>
                        </div>
                        <p className={`text-2xl font-bold ${valueCls}`}>{value}</p>
                        <p className="text-xs text-slate-400 mt-0.5">{sub}</p>
                    </div>
                ))}
            </div>

            {/* Charts row */}
            <div className="grid gap-4 mb-4">
                {/* Status donut */}
                <div className="bg-white rounded-xl border border-border shadow-sm p-5">
                    <div className="mb-4">
                        <h2 className="text-sm font-semibold text-slate-900">Status breakdown</h2>
                        <p className="text-xs text-slate-400 mt-0.5">Current pipeline distribution</p>
                    </div>
                    <ResponsiveContainer width="100%" height={160}>
                        <PieChart>
                            <Pie
                                data={STATUS_BREAKDOWN}
                                cx="50%"
                                cy="50%"
                                innerRadius={48}
                                outerRadius={72}
                                paddingAngle={3}
                                dataKey="value"
                                strokeWidth={0}
                            >
                            </Pie>
                            <Tooltip
                                contentStyle={{
                                    fontSize: 12,
                                    borderRadius: 8,
                                    border: "1px solid #e2e8f0",
                                    fontFamily: "'Plus Jakarta Sans', sans-serif"
                                }}
                            />
                        </PieChart>
                    </ResponsiveContainer>
                    <div className="flex flex-col gap-1.5 mt-2">
                        {STATUS_BREAKDOWN.map(({ name, fill }) => (
                            <div key={name} className="flex items-center justify-between">
                                <div className="flex items-center gap-2">
                                    <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: fill }} />
                                    <span className="text-xs text-slate-600">{name}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}
