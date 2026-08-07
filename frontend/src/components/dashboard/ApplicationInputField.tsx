import { useEffect, useState } from "react";
import type { InputHTMLAttributes } from "react";
import { useDebounce } from "../../hooks/useDebounce";

interface ApplicationInputFieldProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "onChange" | "value"> {
  value: string;
  onValueChange?: (value: string) => void;
  debounceMs: number;
}

export default function ApplicationInputField({
  value,
  onValueChange,
  debounceMs,
  className = "",
  ...props
}: ApplicationInputFieldProps) {
  const [draftValue, setDraftValue] = useState(value);
  const debouncedValue = useDebounce(draftValue, debounceMs);

  useEffect(() => {
    if (debouncedValue !== value) {
      onValueChange?.(debouncedValue);
    }
  }, [debouncedValue, onValueChange, value]);

  return (
    <input
      {...props}
      value={draftValue}
      onChange={(event) => setDraftValue(event.target.value)}
      className={`${className}`.trim()}
    />
  );
}