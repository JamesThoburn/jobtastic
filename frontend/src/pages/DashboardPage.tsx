import axios from "axios";
import { MapPin, Plus, Search, Trash2 } from "lucide-react";
import { useEffect, useMemo, useState } from "react";

type ApplicationStatus =
  | "APPLIED"
  | "INTERVIEWING"
  | "OFFER"
  | "REJECTED";

const ApplicationStatusOptions = [
  { label: "Applied", value: "APPLIED", color: "blue" },
  { label: "Interviewing", value: "INTERVIEWING", color: "amber" },
  { label: "Offer", value: "OFFER", color: "emerald" },
  { label: "Rejected", value: "REJECTED", color: "rose" }
]

interface Application {
  id?: number,
  companyName: string,
  positionName: string,
  status: ApplicationStatus,
  dateApplied?: string | null,
  location?: string | null,
  notes?: string | null,
  createdAt?: string,
  updatedAt?: string
}

type EditableField = "companyName" | "positionName" | "status" | "dateApplied" | "location" | "notes";

export default function DashboardPage() {
  const [applications, setApplications] = useState<Application[]>([]);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL")

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("/api/v1/applications", {
          withCredentials: true
        });
        setApplications(response.data)
      } catch (error) {
        console.error("Failed to fetch", error)
      }
    } 
    fetchData();
  }, [])

  const handleAddApplication = async () => {
    const newApplication: Application = {
      companyName: "",
      positionName: "",
      status: "APPLIED"
    };

    try {
      const response = await axios.post("/api/v1/applications", newApplication, { withCredentials: true });
      setApplications([response.data, ...applications]);
    } catch (error) {
      console.error("Failed to add", error);
    }
  }

  const handleUpdate = async (id: number | undefined, field: EditableField, value: Application[EditableField]) => {
    if (!id) return;

    setApplications((prev) =>
      prev.map((app) => (app.id === id ? { ...app, [field]: value } : app))
    );

    try {
      await axios.patch(`/api/v1/applications/${id}`, { [field]: value }, {
        withCredentials: true
      });
    } catch (error) {
      console.error("Failed to auto-save", error);
    }
  }

  const handleDelete = async (id: number | undefined) => {
    if (!id) return;

    try {
      await axios.delete(`/api/v1/applications/${id}`, { withCredentials: true });
      setApplications((prev) => prev.filter((app) => app.id !== id));
    } catch (error) {
      console.error("Failed to delete application", error)
    }
  }

  const getLastUpdated = () => {
    if (applications.length === 0) return "No applications yet";

    const dates = applications.map((app) => 
      app.updatedAt ? new Date(app.updatedAt).getTime() : 0
    ).filter((time) => time > 0);

    if (dates.length === 0) return "Unknown";

    const lastDate = new Date(Math.max(...dates));
    return lastDate.toLocaleDateString(undefined, {
      month: "long",
      day: "numeric"
    });
  }

  const getStatusStyles = (color: string) => {
    const styles: Record<string, { badge: string, dot: string }> = {
      blue: { badge: "bg-blue-50 text-blue-700 ring-blue-200", dot: "bg-blue-500" },
      amber: { badge: "bg-amber-50 text-amber-700 ring-amber-200", dot: "bg-amber-500" },
      emerald: { badge: "bg-emerald-50 text-emerald-700 ring-emerald-200", dot: "bg-emerald-500" },
      rose: { badge: "bg-rose-50 text-rose-600 ring-rose-200", dot: "bg-rose-400" },
    };
    return styles[color] || { badge: "bg-slate-50 text-slate-700 ring-slate-600/20", dot: "bg-slate-500" };
  };

  const filteredApplications = useMemo(() => {
    return applications.filter((app) => {
      const matchSearch = app.companyName.toLowerCase().includes(search.toLowerCase()) ||
        app.positionName.toLowerCase().includes(search.toLowerCase());
      const matchStatus =
        statusFilter === "ALL" || app.status === statusFilter;
      return matchSearch && matchStatus;
    })
  }, [search, statusFilter, applications])

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
          My Applications
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Tracking {filteredApplications.length} {filteredApplications.length === 1 ? 'application' : 'applications'} {applications.length > 0 && ` — last updated ${getLastUpdated()}`}
        </p>
      </div>

      {/* Table card */}
      <div className="bg-white rounded-xl border border-border shadow-sm overflow-hidden">
        {/* Toolbar */}
        <div className="flex flex-wrap items-center gap-3 p-4 border-b border-border">
          <div className="relative flex-1 min-w-48">
            <Search 
              size={15}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            />
            <input 
              type="text"
              placeholder="Search company or role..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full bg-slate-50 border border-border rounded-lg pl-9 pr-3 py-2 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
            />
          </div>

          {/* Status filter pills */}
          <div className="flex items-center gap-1.5 flex-wrap">
            {(["All", "Applied", "Interviewing", "Offer", "Rejected"] as const).map(
              (s) => (
                <button
                  key={s}
                  onClick={() => setStatusFilter(s.toUpperCase())}
                  className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors hover:cursor-pointer ${
                    statusFilter === s.toUpperCase()
                      ? "bg-slate-900 text-white"
                      : "bg-slate-50 text-slate-600 border border-border hover:bg-slate-100"
                  }`}
                >
                  {s}
                </button>
              )
            )}
          </div>

          <button
            onClick={handleAddApplication}
            className="ml-auto flex items-center gap-2 bg-slate-900 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-slate-800 transition-colors shrink-0 hover:cursor-pointer"
          >
            <Plus size={15} />
            Add Job
          </button>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full min-w-180">
            <thead>
              <tr className="bg-slate-50 border-b border-border">
                {[
                  "Company",
                  "Role",
                  "Status",
                  "Date Applied",
                  "Location / Notes",
                  "",
                ].map((h) => (
                  <th
                    key={h}
                    className="px-4 py-3 text-left text-xs font-semibold text-slate-500 tracking-wide uppercase first:pl-5 last:pr-5"
                    style={h === "" ? { width: 40 } : undefined}
                  >
                    {h}
                  </th>
                ))
                }
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {filteredApplications.map((app) => (
                <tr key={app.id} className="group hover:bg-slate-50/70 transition-colors">
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={app.companyName || ""}
                      onChange={(e) => handleUpdate(app.id, "companyName", e.target.value)}
                      className="w-full bg-transparent border-none focus:ring-0 focus:outline-0 text-slate-900"
                      placeholder="Company name..."
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={app.positionName || ""}
                      onChange={(e) => handleUpdate(app.id, "positionName", e.target.value)}
                      className="w-full bg-transparent border-none focus:ring-0 focus:outline-0 text-slate-900"
                      placeholder="Role name..."
                    />
                  </td>
                  <td className="px-4 py-3">
                    <div className="relative inline-flex items-center">
                      <select
                        value={app.status}
                        onChange={(e) => handleUpdate(app.id, "status", e.target.value)}
                        className={`appearance-none pl-7 pr-3 py-1 rounded-full text-xs font-medium ring-1 ring-inset hover:cursor-pointer focus:outline-0 ${getStatusStyles(ApplicationStatusOptions.find(o => o.value === app.status)?.color || 'slate').badge}`}
                      >
                        {ApplicationStatusOptions.map((s) => (
                          <option
                            key={s.value}
                            value={s.value}
                          >
                            {s.label}
                          </option>
                        ))}
                      </select>
                      <div className={`absolute left-2.5 top-1/2 -mt-1 h-2 w-2 rounded-full ${getStatusStyles(ApplicationStatusOptions.find(o => o.value === app.status)?.color || 'slate').dot}`} />

                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="date"
                      value={app.dateApplied || ""}
                      onChange={(e) => handleUpdate(app.id, "dateApplied", e.target.value)}
                      className="w-full bg-transparent border-none focus:ring-0 focus:outline-0 text-slate-900"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex flex-col gap-0.5 text-xs">
                      <div className="flex items-center gap-1 text-slate-500">
                        <MapPin size={11} className="shrink-0" />
                        <input
                          type="text"
                          value={app.location || ""}
                          onChange={(e) => handleUpdate(app.id, "location", e.target.value)}
                          placeholder="Enter location here..."
                          className="bg-transparent border-none focus:ring-0 focus:outline-0"
                        />
                      </div>
                      <input
                        type="text"
                        value={app.notes || ""}
                        onChange={(e) => handleUpdate(app.id, "notes", e.target.value)}
                        placeholder="Enter notes here..."
                        className="bg-transparent border-none focus:ring-0 focus:outline-0"
                      />
                    </div>
                  </td>

                  <td className="px-4 py-3 text-right">
                    <button
                      onClick={() => handleDelete(app.id)}
                      className="opacity-0 group-hover:opacity-100 p-2 text-slate-400 hover:text-red-600 hover:cursor-pointer transition-opacity"
                    >
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
