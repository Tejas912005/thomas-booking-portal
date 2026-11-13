import React from 'react';
import './Button.css';

export default function Button({
  children,
  variant = 'primary', // 'primary' | 'secondary' | 'ghost' | 'outline' | 'danger'
  size = 'md',        // 'sm' | 'md' | 'lg'
  disabled = false,
  loading = false,
  onClick,
  type = 'button',
  className = '',
  icon = null,
  ...props
}) {
  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={`btn btn-${variant} btn-${size} ${loading ? 'btn-loading' : ''} ${className}`}
      {...props}
    >
      {loading && <span className="btn-spinner" />}
      {!loading && icon && <span className="btn-icon">{icon}</span>}
      <span className="btn-text">{children}</span>
    </button>
  );
}

