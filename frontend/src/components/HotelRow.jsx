import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './HotelRow.css';
import HotelVisual from './HotelVisual';
import { formatINR } from '../utils/format';
import { ArrowRightIcon } from './Icons';

export default function HotelRow({ hotel, checkIn = '', checkOut = '' }) {
  const [isHovered, setIsHovered] = useState(false);

  const queryParams = new URLSearchParams();
  if (checkIn) queryParams.set('checkIn', checkIn);
  if (checkOut) queryParams.set('checkOut', checkOut);
  const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';

  const amenitiesList = hotel.amenities ? hotel.amenities.split(',').map((s) => s.trim()) : [];

  return (
    <article
      className="hotel-row"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <Link to={`/hotels/${hotel.id}${queryString}`} className="hotel-row-link">
        <div className="hotel-row-main">
          <div className="hotel-row-header">
            <span className="hotel-city-badge mono">{hotel.location.toUpperCase()}</span>
            <span className="hotel-inventory-count mono">
              {hotel.availableRooms != null ? (
                hotel.availableRooms <= 2 ? (
                  <strong className="last-rooms-alert">ONLY {hotel.availableRooms} ROOM{hotel.availableRooms === 1 ? '' : 'S'} LEFT</strong>
                ) : (
                  `${hotel.availableRooms} rooms available`
                )
              ) : (
                `${hotel.totalRooms} rooms total`
              )}
            </span>
          </div>

          <h2 className="hotel-row-name serif-heading">{hotel.name}</h2>

          <p className="hotel-row-amenities">
            {amenitiesList.slice(0, 4).join('  ·  ')}
          </p>

          {/* Reveal preview on hover/focus */}
          <div className={`hotel-row-expandable ${isHovered ? 'is-expanded' : ''}`}>
            <p className="hotel-row-desc">{hotel.description}</p>
          </div>
        </div>

        {/* Right side: Visual + Price */}
        <div className="hotel-row-aside">
          <div className="hotel-row-visual-box">
            <HotelVisual
              id={hotel.id}
              name={hotel.name}
              location={hotel.location}
              width="140px"
              height="95px"
            />
          </div>

          <div className="hotel-row-price-box">
            <div className="hotel-price-figure mono">
              <span className="hotel-currency">₹</span>
              <span className="hotel-amount">{formatINR(hotel.pricePerNight)}</span>
            </div>
            <span className="hotel-rate-caption">per night</span>
            <span className="hotel-action-arrow" aria-hidden="true">
              <ArrowRightIcon size={14} />
            </span>
          </div>
        </div>
      </Link>
    </article>
  );
}

