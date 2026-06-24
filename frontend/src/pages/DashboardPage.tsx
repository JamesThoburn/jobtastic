import axios from "axios";
import { Plus } from "lucide-react";
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
                <tr key={app.id}>
                  <td className="px-4 py-3">
                    {app.companyName}
                  </td>
                  <td className="px-4 py-3">
                    {app.positionName}
                  </td>
                  <td className="px-4 py-3">
                    {app.status.substring(0,1).toUpperCase()}{app.status.substring(1,app.status.length).toLowerCase()}
                  </td>
                  <td className="px-4 py-3">
                    {app.dateApplied}
                  </td>
                  <td className="px-4 py-3">
                    {app.location} {app.notes}
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
