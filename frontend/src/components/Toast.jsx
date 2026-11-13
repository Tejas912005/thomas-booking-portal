import React, { useEffect } from 'react';
import './Toast.css';
import { CheckIcon, CrossIcon } from './Icons';

export default function Toast({ message, type = 'info', onClose, duration = 3000 }) {
  useEffect(() => {
    if (!duration) return;
    const timer = setTimeout(() => {
      onClose();
    }, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  return (
    <div className={`editorial-toast toast-${type}`} role="alert">
      <span className="toast-icon">
        {type === 'success' ? <CheckIcon size={16} /> : <CrossIcon size={16} />}
      </span>
      <span className="toast-message">{message}</span>
      <button className="toast-close" onClick={onClose} aria-label="Dismiss">
        &times;
      </button>
    </div>
  );
}

