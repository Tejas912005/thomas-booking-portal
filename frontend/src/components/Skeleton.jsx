import React from 'react';
import './Skeleton.css';

export default function Skeleton({ width = '100%', height = '20px', className = '', style = {} }) {
  return (
    <div
      className={`skeleton-line ${className}`}
      style={{ width, height, ...style }}
      aria-hidden="true"
    />
  );
}

export function HotelListSkeleton() {
  return (
    <div className="skeleton-hotel-list">
      {[1, 2, 3].map((i) => (
        <div key={i} className="skeleton-hotel-row">
          <div className="skeleton-hotel-left">
            <Skeleton width="120px" height="12px" style={{ marginBottom: '8px' }} />
            <Skeleton width="280px" height="28px" style={{ marginBottom: '12px' }} />
            <Skeleton width="220px" height="14px" />
          </div>
          <div className="skeleton-hotel-right">
            <Skeleton width="90px" height="24px" style={{ marginBottom: '6px' }} />
            <Skeleton width="60px" height="12px" />
          </div>
        </div>
      ))}
    </div>
  );
}

