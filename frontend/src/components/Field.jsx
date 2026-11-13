import React from 'react';
import './Field.css';

export default function Field({
  label,
  id,
  type = 'text',
  value,
  onChange,
  error,
  hint,
  placeholder,
  required = false,
  disabled = false,
  className = '',
  mono = false,
  ...props
}) {
  return (
    <div className={`field-group ${error ? 'has-error' : ''} ${className}`}>
      {label && (
        <label htmlFor={id} className="field-label">
          {label}
          {required && <span className="field-required">*</span>}
        </label>
      )}
      <div className="field-input-wrapper">
        <input
          id={id}
          type={type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          disabled={disabled}
          required={required}
          className={`field-input ${mono ? 'mono' : ''}`}
          aria-invalid={!!error}
          aria-describedby={error ? `${id}-error` : hint ? `${id}-hint` : undefined}
          {...props}
        />
      </div>
      {hint && !error && <p id={`${id}-hint`} className="field-hint">{hint}</p>}
      {error && <p id={`${id}-error`} className="field-error">{error}</p>}
    </div>
  );
}

