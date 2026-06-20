import { useNavigate } from 'react-router-dom'

export default function Logo() {
    const navigate = useNavigate();

    return (
        <div
            onClick={() => navigate("/")}
            className="flex items-center gap-2 hover:cursor-pointer"
        >
            <div className="w-7 h-7 bg-slate-900 rounded-md flex items-center justify-center">
                <span className="text-white text-xs font-bold">J</span>
            </div>
            <span className="text-slate-900 font-bold text-lg tracking-tight">
                Jobtastic
            </span>
        </div>
    )
}
