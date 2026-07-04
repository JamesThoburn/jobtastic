import { Clock, Mail, ShieldCheck, User } from "lucide-react";
import { useState, type FormEvent } from "react";
import apiService from "../services/apiService";
import { useAuth } from "../hooks/useAuth";
import Button from "../components/ui/Button";

const TABS = [{ id: "account" as const, label: "Account" }];

type ProfileFormState = {
  firstName: string;
  lastName: string;
  email: string;
};

type PasswordFormState = {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
};

const getProfileFormState = (currentUser: { firstName?: string; lastName?: string; email?: string } | null | undefined): ProfileFormState => ({
  firstName: currentUser?.firstName ?? "",
  lastName: currentUser?.lastName ?? "",
  email: currentUser?.email ?? "",
});

export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const [activeTab, setActiveTab] = useState<"account">("account");
  const [profileForm, setProfileForm] = useState<ProfileFormState>(() => getProfileFormState(user));
  const [passwordForm, setPasswordForm] = useState<PasswordFormState>({
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: "",
  });
  const [profileSaving, setProfileSaving] = useState(false);
  const [passwordSaving, setPasswordSaving] = useState(false);
  const [profileMessage, setProfileMessage] = useState<string | null>(null);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [passwordMessage, setPasswordMessage] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const profileFormKey = user?.email ?? user?.id ?? "guest";

  const memberSince = user?.createdAt
    ? new Date(user.createdAt).toLocaleDateString(undefined, {
        month: "long",
        year: "numeric",
      })
    : "";

  const initials = user?.firstName?.[0] ?? user?.lastName?.[0] ?? "U";

  const handleProfileSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setProfileSaving(true);
    setProfileError(null);
    setProfileMessage(null);

    try {
      const { data } = await apiService.patch("/users/me", {
        firstName: profileForm.firstName.trim(),
        lastName: profileForm.lastName.trim(),
        email: profileForm.email.trim(),
      });

      updateUser(data);
      setProfileMessage("Profile updated successfully.");
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      setProfileError(axiosError.response?.data?.message ?? "Unable to update profile right now.");
    } finally {
      setProfileSaving(false);
    }
  };

  const handlePasswordSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setPasswordSaving(true);
    setPasswordError(null);
    setPasswordMessage(null);

    try {
      await apiService.patch("/users/me/password", {
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
        confirmNewPassword: passwordForm.confirmNewPassword,
      });

      setPasswordForm({ currentPassword: "", newPassword: "", confirmNewPassword: "" });
      setPasswordMessage("Password updated successfully.");
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      setPasswordError(axiosError.response?.data?.message ?? "Unable to update password right now.");
    } finally {
      setPasswordSaving(false);
    }
  };

  return (
    <div className="p-6 max-w-4xl mx-auto w-full">
      <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden mb-4">
        <div className="h-24 bg-slate-100 relative"></div>

        <div className="px-6 pb-6">
          <div className="flex items-end justify-between -mt-8 mb-4">
            <div className="relative">
              <div className="w-16 h-16 rounded-2xl bg-indigo-600 text-white text-2xl font-bold flex uppercase items-center justify-center ring-4 ring-white shadow-sm">
                {initials}
              </div>
            </div>
          </div>

          <div className="mb-4">
            <h1 className="text-xl font-bold text-slate-900 mb-0.5">
              {user?.firstName} {user?.lastName}
            </h1>
          </div>

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

      <div className="flex gap-1 mb-6 bg-slate-100 p-1 rounded-xl w-fit">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors hover:cursor-pointer ${
              activeTab === tab.id
                ? "bg-white text-slate-900 shadow-sm"
                : "text-slate-500 hover:text-slate-700"
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === "account" && (
        <div className="flex flex-col gap-5 max-w-xl">
          <form key={profileFormKey} onSubmit={handleProfileSubmit} className="bg-white rounded-2xl border border-border shadow-sm p-6">
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
                    value={profileForm.firstName}
                    onChange={(event) =>
                      setProfileForm((current) => ({ ...current, firstName: event.target.value }))
                    }
                    className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                  />
                </div>
                <div className="flex flex-col gap-1.5 w-[50%]">
                  <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                    Last name
                  </label>
                  <input
                    value={profileForm.lastName}
                    onChange={(event) =>
                      setProfileForm((current) => ({ ...current, lastName: event.target.value }))
                    }
                    className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                  />
                </div>
              </div>

              <div className="flex flex-col gap-1.5 w-full">
                <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  Email
                </label>
                <input
                  value={profileForm.email}
                  onChange={(event) =>
                    setProfileForm((current) => ({ ...current, email: event.target.value }))
                  }
                  type="email"
                  className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                />
              </div>

              {profileMessage ? <p className="text-sm text-emerald-600">{profileMessage}</p> : null}
              {profileError ? <p className="text-sm text-rose-600">{profileError}</p> : null}

              <Button variant="primary" size="sm" className="w-fit font-semibold mt-2" disabled={profileSaving}>
                {profileSaving ? "Saving..." : "Save changes"}
              </Button>
            </div>
          </form>

          <form onSubmit={handlePasswordSubmit} className="bg-white rounded-2xl border border-border shadow-sm p-6">
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
                  value={passwordForm.currentPassword}
                  onChange={(event) =>
                    setPasswordForm((current) => ({ ...current, currentPassword: event.target.value }))
                  }
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
                  value={passwordForm.newPassword}
                  onChange={(event) =>
                    setPasswordForm((current) => ({ ...current, newPassword: event.target.value }))
                  }
                  type="password"
                  placeholder="Enter new password"
                  className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  Confirm new password
                </label>
                <input
                  value={passwordForm.confirmNewPassword}
                  onChange={(event) =>
                    setPasswordForm((current) => ({ ...current, confirmNewPassword: event.target.value }))
                  }
                  type="password"
                  placeholder="Confirm new password"
                  className="bg-slate-50 border border-border rounded-lg px-3.5 py-2.5 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400 transition-all"
                />
              </div>

              {passwordMessage ? <p className="text-sm text-emerald-600">{passwordMessage}</p> : null}
              {passwordError ? <p className="text-sm text-rose-600">{passwordError}</p> : null}

              <Button variant="primary" size="sm" className="w-fit font-semibold mt-2" disabled={passwordSaving}>
                {passwordSaving ? "Updating..." : "Update password"}
              </Button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
