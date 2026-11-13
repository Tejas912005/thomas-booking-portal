import React from 'react';
import './EmptyState.css';

export default function EmptyState({
  title = 'No destinations found',
  description = 'We could not find any properties matching your current criteria.',
  action = null,
  icon = null,
  className = '',
}) {
  return (
    <div className={`empty-state ${className}`}>
      {icon && <div className="empty-state-icon">{icon}</div>}
      <h3 className="empty-state-title serif-heading">{title}</h3>
      <p className="empty-state-description">{description}</p>
      {action && <div className="empty-state-action">{action}</div>}
    </div>
  );
}

