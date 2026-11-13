import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './HomePage.css';
import DateRangePicker from '../components/DateRangePicker';
import HotelVisual from '../components/HotelVisual';
import { ArrowRightIcon, CalendarIcon, MapPinIcon, SearchIcon, CheckIcon } from '../components/Icons';
import { getTodayString, addDays, calculateNights, formatEditorialDate } from '../utils/dates';
import { formatINR } from '../utils/format';
import { SEED_HOTELS } from '../api/mockData';

export default function HomePage() {
  const navigate = useNavigate();
  const today = getTodayString();
  const defaultCheckIn = addDays(today, 7);
  const defaultCheckOut = addDays(today, 10);

  const [location, setLocation] = useState('');
  const [dates, setDates] = useState({
    checkIn: defaultCheckIn,
    checkOut: defaultCheckOut,
  });
  const [showDatePicker, setShowDatePicker] = useState(false);

  const popularCities = ['Mumbai', 'Goa', 'Shimla', 'Bangalore', 'Jaisalmer', 'Coorg'];
  const nights = calculateNights(dates.checkIn, dates.checkOut);

  const handleSearch = (e) => {
    if (e) e.preventDefault();
    const params = new URLSearchParams();
    if (location) params.set('location', location);
    if (dates.checkIn) params.set('checkIn', dates.checkIn);
    if (dates.checkOut) params.set('checkOut', dates.checkOut);
    navigate(`/hotels?${params.toString()}`);
  };

  const handleQuickCity = (city) => {
    setLocation(city);
    const params = new URLSearchParams();
    params.set('location', city);
    if (dates.checkIn) params.set('checkIn', dates.checkIn);
    if (dates.checkOut) params.set('checkOut', dates.checkOut);
    navigate(`/hotels?${params.toString()}`);
  };

  return (
    <div className="home-wrapper">
      {/* ── Beautiful Hero Section with Full Resort Image ── */}
      <section className="home-hero-banner">
        <div className="hero-bg-wrapper">
          <div className="hero-background-image" />
          <div className="hero-dark-overlay" />
        </div>

        <div className="hero-content">
          <div className="hero-tag">
            <span className="hero-tag-dot" />
            <span>HANDPICKED HOTELS ACROSS INDIA</span>
          </div>

          <h1 className="hero-title">
            Find and book your perfect stay.
          </h1>

          <p className="hero-subtitle">
            From serene beach resorts in Goa to heritage palaces in Rajasthan and mountain lodges in Himachal. Direct booking with live room availability and instant confirmation.
          </p>

          {/* ── Modern Search Console ── */}
          <div className="search-console-container">
            <form onSubmit={handleSearch} className="search-console-bar">
              {/* Destination */}
              <div className="console-cell cell-destination">
                <label htmlFor="destSelect" className="console-label">
                  <MapPinIcon size={14} />
                  <span>DESTINATION</span>
                </label>
                <div className="console-select-wrapper">
                  <select
                    id="destSelect"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    className="console-select"
                  >
                    <option value="">All Destinations</option>
                    {popularCities.map((c) => (
                      <option key={c} value={c}>{c}, India</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="console-divider" aria-hidden="true" />

              {/* Check-In */}
              <div
                className="console-cell cell-date"
                onClick={() => setShowDatePicker(!showDatePicker)}
                role="button"
                tabIndex={0}
              >
                <span className="console-label">
                  <CalendarIcon size={14} />
                  <span>CHECK-IN</span>
                </span>
                <span className="console-val">
                  {dates.checkIn ? formatEditorialDate(dates.checkIn) : 'Choose Date'}
                </span>
                <span className="console-sub">After 2:00 PM</span>
              </div>

              <div className="console-divider" aria-hidden="true" />

              {/* Check-Out */}
              <div
                className="console-cell cell-date"
                onClick={() => setShowDatePicker(!showDatePicker)}
                role="button"
                tabIndex={0}
              >
                <span className="console-label">
                  <CalendarIcon size={14} />
                  <span>CHECK-OUT ({nights} {nights === 1 ? 'Night' : 'Nights'})</span>
                </span>
                <span className="console-val">
                  {dates.checkOut ? formatEditorialDate(dates.checkOut) : 'Choose Date'}
                </span>
                <span className="console-sub">Before 11:00 AM</span>
              </div>

              {/* Search Button */}
              <div className="console-action-slot">
                <button type="submit" className="console-submit-btn">
                  <SearchIcon size={16} />
                  <span>Search Hotels</span>
                </button>
              </div>
            </form>

            {/* Calendar Popover with Full Dual-Month View */}
            {showDatePicker && (
              <div className="console-calendar-popover">
                <DateRangePicker
                  checkIn={dates.checkIn}
                  checkOut={dates.checkOut}
                  onChange={(d) => {
                    setDates(d);
                    if (d.checkIn && d.checkOut) {
                      setShowDatePicker(false);
                    }
                  }}
                  onClose={() => setShowDatePicker(false)}
                />
              </div>
            )}
          </div>

          {/* Quick Destination Pills */}
          <div className="quick-cities-row">
            <span className="quick-label">POPULAR CITIES:</span>
            {popularCities.map((city) => (
              <button
                key={city}
                type="button"
                onClick={() => handleQuickCity(city)}
                className="quick-city-pill"
              >
                {city}
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* ── Featured Hotels Section ── */}
      <section className="home-portfolio-section">
        <div className="portfolio-section-header">
          <div>
            <span className="section-eyebrow">FEATURED STAYS</span>
            <h2 className="section-title">Popular Hotels & Resorts</h2>
            <p className="section-desc">Handpicked stays across top destinations with guaranteed availability</p>
          </div>
          <Link to="/hotels" className="portfolio-view-all">
            View All Hotels ({SEED_HOTELS.length}) →
          </Link>
        </div>

        <div className="portfolio-editorial-grid">
          {SEED_HOTELS.map((hotel) => (
            <article key={hotel.id} className="portfolio-card">
              <Link
                to={`/hotels/${hotel.id}?checkIn=${dates.checkIn}&checkOut=${dates.checkOut}`}
                className="card-media-link"
              >
                <div className="card-visual-wrapper">
                  <HotelVisual
                    id={hotel.id}
                    name={hotel.name}
                    location={hotel.location}
                    imageUrl={hotel.imageUrl}
                    height="210px"
                    alt={hotel.name}
                  />
                  
                  {/* Badge */}
                  <div className="card-floating-badge">
                    {hotel.totalRooms <= 2 ? (
                      <span className="badge-scarce">🔥 Only {hotel.totalRooms} rooms total</span>
                    ) : (
                      <span>{hotel.tag || hotel.location}</span>
                    )}
                  </div>
                </div>
              </Link>

              <div className="card-body">
                <div className="card-meta-line">
                  <span className="card-location-label">📍 {hotel.location}, INDIA</span>
                  <span className="card-rating-badge">★ {hotel.rating || 4.8} ({hotel.reviewsCount || 120})</span>
                </div>

                <h3 className="card-title">
                  <Link to={`/hotels/${hotel.id}?checkIn=${dates.checkIn}&checkOut=${dates.checkOut}`}>
                    {hotel.name}
                  </Link>
                </h3>

                <p className="card-description">{hotel.description}</p>

                <div className="card-amenities-snippet">
                  {hotel.amenities.split(',').slice(0, 3).map((a) => a.trim()).join(' · ')}
                </div>

                <div className="card-footer-row">
                  <div className="card-rate-box">
                    <span className="card-rate-caption">PRICE PER NIGHT</span>
                    <span className="card-rate-val">
                      ₹{formatINR(hotel.pricePerNight)} <span className="rate-unit">/ night</span>
                    </span>
                  </div>

                  <Link
                    to={`/hotels/${hotel.id}?checkIn=${dates.checkIn}&checkOut=${dates.checkOut}`}
                    className="card-explore-btn"
                  >
                    <span>View Deal</span>
                    <ArrowRightIcon size={14} />
                  </Link>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>

      {/* ── Why Book With Us Section (Simple English) ── */}
      <section className="home-benefits-section">
        <div className="benefits-header">
          <span className="section-eyebrow">WHY BOOK WITH US</span>
          <h2 className="section-title">A better way to book your stay</h2>
          <p className="section-desc">We make hotel booking honest, seamless, and completely reliable.</p>
        </div>

        <div className="benefits-grid">
          <div className="benefit-card">
            <div className="benefit-icon">🔒</div>
            <h4 className="benefit-title">Guaranteed Room Availability</h4>
            <p className="benefit-desc">
              Every room is locked in real-time in our database. You will never arrive to find your room was double-booked.
            </p>
          </div>

          <div className="benefit-card">
            <div className="benefit-icon">🏷️</div>
            <h4 className="benefit-title">Clear & Honest Pricing</h4>
            <p className="benefit-desc">
              See the exact price for your full stay before you confirm. No surprise service dues, hidden taxes, or checkout fees.
            </p>
          </div>

          <div className="benefit-card">
            <div className="benefit-icon">🎟️</div>
            <h4 className="benefit-title">Instant Printable Hotel Pass</h4>
            <p className="benefit-desc">
              Get an official digital booking pass with a unique reference number (HB-XXXXXX) ready to save or print on a single page.
            </p>
          </div>

          <div className="benefit-card">
            <div className="benefit-icon">📅</div>
            <h4 className="benefit-title">Smart Alternative Dates</h4>
            <p className="benefit-desc">
              If your chosen hotel is full on your dates, our system instantly suggests the closest available dates so you don't miss out.
            </p>
          </div>
        </div>
      </section>

      {/* ── Simple, Clean Footer ── */}
      <footer className="home-simple-footer">
        <div className="footer-inner">
          <div className="footer-brand">
            <span className="footer-logo">Grand Tour Hotels</span>
            <span className="footer-text">Handpicked stays across India with instant booking.</span>
          </div>

          <div className="footer-links">
            <Link to="/hotels">All Hotels</Link>
            <span className="sep">•</span>
            <Link to="/manage">Find My Booking</Link>
            <span className="sep">•</span>
            <a href="#destSelect">Check Dates</a>
          </div>

          <div className="footer-copy">
            <span>© 2026 Grand Tour Hotels. All rights reserved.</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
