import type { ButtonHTMLAttributes } from "react";

const VARIANTS = {
    default: "",
    primary: "bg-slate-900 text-white hover:bg-slate-800",
    outline: "bg-white text-slate-700 font-semibold border border-border hover:bg-slate-50 transition-colors",
    ghost: "text-slate-600 hover:text-slate-900 bg-transparent",
};

const SIZES = {
    sm: "text-sm px-4 py-2 rounded-lg",
    md: "text-base px-6 py-3 rounded-xl",
};

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: keyof typeof VARIANTS;
    size?: keyof typeof SIZES;
}

export default function Button({
    children,
    variant = "default",
    size = "md",
    className = "",
    disabled,
    ...props
}: ButtonProps) {
    const baseClasses = "inline-flex items-center hover:cursor-pointer transition-colors";
    const computedClasses = `${baseClasses} ${VARIANTS[variant]} ${SIZES[size]} ${className}`.trim();

    return (
        <button
            disabled={disabled}
            className={computedClasses}
            {...props}
        >
            {children}
        </button>
    )
}
