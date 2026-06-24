import axios from "axios";
import { MapPin, Plus, Trash2 } from "lucide-react";
import { useEffect, useState } from "react";

type ApplicationStatus =
  | "APPLIED"
  | "INTERVIEWING"
  | "OFFER"
  | "REJECTED";

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

export default function DashboardPage() {
  const [applications, setApplications] = useState<Application[]>([]);

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

  const handleUpdate = async (id: number | undefined, field: string, value: any) => {
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
      await axios.delete(`/api/v1/applications/${id}`, {withCredentials: true});
      setApplications((prev) => prev.filter((app) => app.id !== id));
    } catch (error) {
      console.error("Failed to delete application", error)
    }
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
          My Applications
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Tracking {applications.length} applications — last updated today
        </p>
      </div>

      {/* Table card */}
      <div className="bg-white rounded-xl border border-border shadow-sm overflow-hidden">
        {/* Toolbar */}
        <div className="flex flex-wrap items-center gap-3 p-4 border-b border-border">
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
              {applications.map((app) => (
                <tr key={app.id} className="group">
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
                    <select
                      value={app.status}
                      onChange={(e) => handleUpdate(app.id, "status", e.target.value)}
                      className="w-full bg-transparent border-none focus:ring-0 focus:outline-0 text-slate-900"
                    >
                      <option value="APPLIED">Applied</option>
                      <option value="INTERVIEWING">Interviewing</option>
                      <option value="OFFER">Offer</option>
                      <option value="REJECTED">Rejected</option>
                    </select>
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
