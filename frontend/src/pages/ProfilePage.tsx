import { Clock, Mail, ShieldCheck, User } from "lucide-react";
import { useAuth } from "../context/AuthContext"
import { useEffect, useState } from "react";
import Button from "../components/ui/Button";

const TABS = [
  { id: "account" as const, label: "Account" }
]

export default function ProfilePage() {
  const { user } = useAuth();
  const [memberSince, setMemberSince] = useState("");
  const [activeTab, setActiveTab] = useState<"account">("account");

  useEffect(() => {
    if (user != null) {
      const createdAt = new Date(user.createdAt);
      setMemberSince(
        createdAt.toLocaleDateString(undefined, { month: "long", year: "numeric" })
      )
    }
  }, [user])

  return (
    <div className="p-6 max-w-4xl mx-auto w-full">
      {/* Header card */}
      <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden mb-4">
        {/* Cover strip */}
        <div className="h-24 bg-slate-100 relative">
        </div>

        <div className="px-6 pb-6">
          {/* Avatar row */}
          <div className="flex items-end justify-between -mt-8 mb-4">
            <div className="relative">
              <div className="w-16 h-16 rounded-2xl bg-indigo-600 text-white text-2xl font-bold flex uppercase items-center justify-center ring-4 ring-white shadow-sm">
                {user?.firstName.substring(0, 1)}
              </div>
            </div>
          </div>

          {/* Name */}
          <div className="mb-4">
            <h1 className="text-xl font-bold text-slate-900 mb-0.5">
              {user?.firstName} {user?.lastName}
            </h1>
          </div>

          {/* Details row */}
          <div className="flex flex-wrap gap-4 text-xs text-slate-500">
            <span className="flex items-center gap-1.5">
              <Mail size={12} />
              {user?.email}
            </span>
            <span className="flex items-center gap-1.5">
              <Clock size={12} />
              Member since {memberSince}
            </span>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-6 bg-slate-100 p-1 rounded-xl w-fit">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors hover:cursor-pointer ${activeTab === tab.id
              ? "bg-white text-slate-900 shadow-sm"
              : "text-slate-500 hover:text-slate-700"
              }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* --- Account tab --- */}
      {activeTab === "account" && (
        <div className="flex flex-col gap-5 max-w-xl">
          <div className="bg-white rounded-2xl border border-border shadow-sm p-6">
            <h2 className="text-sm font-semibold text-slate-900 mb-5 flex items-center gap-2">
              <User size={15} className="text-slate-500" />
              Account details
            </h2>
            <div className="flex flex-col gap-4">
              <div className="flex gap-2">
                <div className="flex flex-col gap-1.5 w-[50%]">
                  <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                    First name
                  </label>
                  <input
                    defaultValue={user?.firstName}
                    className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                  />
                </div>
                <div className="flex flex-col gap-1.5 w-[50%]">
                  <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                    Last name
                  </label>
                  <input
                    defaultValue={user?.lastName}
                    className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                  />
                </div>
              </div>

              <div className="flex flex-col gap-1.5 w-full">
                <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  Email
                </label>
                <input 
                  defaultValue={user?.email}
                  type="email"
                  className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                />
              </div>

              <Button
                variant="primary"
                size="sm"
                className="w-fit font-semibold mt-2"
              >
                Save changes
              </Button>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-border shadow-sm p-6">
            <h2 className="text-sm font-semibold text-slate-900 mb-5 flex items-center gap-2">
              <ShieldCheck size={15} className="text-slate-500" />
              Change password
            </h2>
            <div className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  Current password
                </label>
                <input 
                  type="password"
                  placeholder="••••••••••"
                  className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  New Password
                </label>
                <input 
                  type="password"
                  placeholder="Enter new password"
                  className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                />
              </div>

              <Button
                variant="primary"
                size="sm"
                className="w-fit font-semibold mt-2"
              >
                Update password
              </Button>
            </div>

          </div>

        </div>
      )}

    </div>
  )
}
