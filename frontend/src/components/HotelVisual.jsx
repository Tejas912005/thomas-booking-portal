import React, { useState } from 'react';

// High quality curated hotel photos matching the hotel destinations
const HOTEL_IMAGES = {
  1: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80',
  2: 'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1200&q=80',
  3: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80',
  4: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1200&q=80',
  5: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80',
  6: 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1200&q=80',
};

export default function HotelVisual({
  id = 1,
  name = '',
  location = '',
  imageUrl = '',
  className = '',
  width = '100%',
  height = '100%',
  alt = '',
}) {
  const [imageError, setImageError] = useState(false);
  const resolvedUrl = imageUrl || HOTEL_IMAGES[id] || HOTEL_IMAGES[1];

  if (!imageError && resolvedUrl) {
    return (
      <div
        className={`hotel-photo-container ${className}`}
        style={{
          width,
          height,
          position: 'relative',
          overflow: 'hidden',
          backgroundColor: '#1E1C1A',
        }}
      >
        <img
          src={resolvedUrl}
          alt={alt || name || 'Hotel view'}
          onError={() => setImageError(true)}
          loading="lazy"
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            display: 'block',
            transition: 'transform 0.4s ease',
          }}
        />
        <div
          style={{
            position: 'absolute',
            inset: 0,
            background: 'linear-gradient(to top, rgba(15, 13, 11, 0.45) 0%, transparent 40%)',
            pointerEvents: 'none',
          }}
        />
      </div>
    );
  }

  // Graceful fallback to SVG if image fails
  const palette = [
    { bg: '#EFE8DD', primary: '#E4572E', secondary: '#DCD4C5', accent: '#16130F' },
    { bg: '#EBF0EA', primary: '#7A8B78', secondary: '#364336', accent: '#E4572E' },
    { bg: '#F2EDE4', primary: '#2D312E', secondary: '#DCD4C5', accent: '#E4572E' },
    { bg: '#FAF5ED', primary: '#C86D51', secondary: '#8A9A86', accent: '#26211C' },
    { bg: '#ECE7DC', primary: '#9E5B44', secondary: '#C4BCAB', accent: '#3A332B' },
    { bg: '#E8ECE6', primary: '#4A6250', secondary: '#E4572E', accent: '#2E3530' },
  ];

  const theme = palette[(id || 1) % palette.length];
  const initial = name ? name.charAt(0).toUpperCase() : 'H';
  const cityCode = location ? location.slice(0, 3).toUpperCase() : 'IND';

  return (
    <svg
      viewBox="0 0 400 280"
      width={width}
      height={height}
      className={className}
      style={{ display: 'block', backgroundColor: theme.bg }}
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="400" height="280" fill={theme.bg} />
      <circle cx="200" cy="140" r="70" fill={theme.primary} opacity="0.8" />
      <rect x="60" y="80" width="160" height="120" fill={theme.secondary} opacity="0.6" />
      <text
        x="30"
        y="60"
        fontFamily="sans-serif"
        fontSize="48"
        fontWeight="bold"
        fill={theme.accent}
        opacity="0.3"
      >
        {initial}
      </text>
      <text
        x="40"
        y="250"
        fontFamily="monospace"
        fontSize="12"
        fill={theme.accent}
        fontWeight="600"
      >
        {cityCode} · {name || 'HOTEL'}
      </text>
    </svg>
  );
}
