import { Bell, ChevronDown, LayoutDashboard, LogOut, Menu, X } from "lucide-react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import Logo from "./Logo";

const NAV_ITEMS = [
    { label: "Applications", icon: LayoutDashboard, active: true }
]

export default function DashboardLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleSignOut = async () => {
    try {
      await logout();
      navigate("/login")
    } catch (error) {
      console.error("Sign out failed", error);
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 flex">
      {sidebarOpen && (
        <div 
          className="fixed inset-0 bg-black/30 z-20 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed lg:static inset-y-0 left-0 z-30 w-60 bg-white border-r border-border flex flex-col justify-between transition-transform duration-200 
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"}`}
      >
        <div className="h-16 flex justify-between items-center px-5 border-b border-border shrink-0">
          <Logo />
          <button
            className="m1-auto lg:hidden text-slate-400 hover:text-slate-600 hover:cursor-pointer duration-200 transition-colors"
            onClick={() => setSidebarOpen(false)}
          >
            <X size={18} />
          </button>
        </div>

        <nav className="flex-1 px-3 py-4 flex flex-col gap-0.5">
            {NAV_ITEMS.map(({ label, icon: Icon }) => {
                const isActive = (
                    label === "Applications" && location.pathname === "/dashboard"
                );
                return (
                    <button
                        key={label}
                        onClick={() => {
                            if (label === "Applications") navigate("dashboard");
                        }}
                        className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors hover:cursor-pointer ${
                            isActive
                            ? "bg-slate-100 text-slate-900"
                            : "text-slate-500 hover:text-slate-900 hover:bg-slate-50"
                        }`}
                    >
                        <Icon size={16} className={isActive ? "text-slate-700" : ""} />
                        {label}
                    </button>
                )
            })}
        </nav>

        <div className="px-3 py-4 border-t border-border">
          <div 
            onClick={() => navigate("/profile")}
            className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-slate-50 transition-colors hover:cursor-pointer"
          >
            <div className="w-7 h-7 rounded-full bg-indigo-100 text-indigo-700 text-xs font-bold flex items-center justify-center shrink-0 uppercase">
              {user?.firstName.substring(0,1)}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-slate-800 truncate">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-xs text-slate-400 truncate">
                {user?.email}
              </p>
            </div>
          </div>
          <button
            onClick={handleSignOut}
            className="w-full mt-1 flex items-center gap-2 px-3 py-2 rounded-lg text-xs text-slate-400 hover:text-slate-600 hover:bg-slate-50 transition-colors hover:cursor-pointer"
          >
            <LogOut size={13}/>
            Sign out
          </button>
        </div>
      </aside>

      <main className="flex-1 min-w-0 flex flex-col">
          <header className="h-16 bg-white border-b border-border flex items-center px-6 gap-4 shrink-0">
            <button
              className="lg:hidden text-slate-500 hover:text-slate-900"
              onClick={() => setSidebarOpen(true)}
            >
              <Menu size={20} />
            </button>
            <div className="flex-1" />
            <div className="w-px h-5 bg-border" />
            <div
              onClick={() => navigate("/profile")}
              className="flex items-center gap-2.5 hover:cursor-pointer"
            >
              <div className="w-7 h-7 rounded-full bg-indigo-100 text-indigo-700 text-xs font-bold flex items-center justify-center">
                {user?.firstName.substring(0,1)}
              </div>
              <span className="text-sm font-medium text-slate-700 hidden sm:block">
                {user?.firstName} {user?.lastName}
              </span>
            </div>
          </header>

          <div className="flex-1 overflow-auto p-6">
            <Outlet />
          </div>
      </main>
    </div>
  )
}
