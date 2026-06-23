import Logo from "../components/layout/Logo";
import { Bell, ChevronDown, LogOut, Menu, X } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

export default function DashboardPage() {
  const { user, logout } = useAuth();
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

  useEffect(() => {
    console.log("Full user object from API:", user)
  },[user]);

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

        <div className="px-3 py-4 border-t border-border">
          <div className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-slate-50 transition-colors hover:cursor-pointer">
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
    </div>
  )
}
