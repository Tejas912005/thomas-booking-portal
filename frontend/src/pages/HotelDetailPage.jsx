import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate, Link } from 'react-router-dom';
import './HotelDetailPage.css';
import { fetchHotelById } from '../api/hotels';
import { fetchBookingQuote } from '../api/bookings';
import HotelVisual from '../components/HotelVisual';
import DateRangePicker from '../components/DateRangePicker';
import Ticket from '../components/Ticket';
import Button from '../components/Button';
import Skeleton from '../components/Skeleton';
import EmptyState from '../components/EmptyState';
import { ArrowLeftIcon, ArrowRightIcon, CheckIcon } from '../components/Icons';
import { getTodayString, addDays, calculateNights, formatShortDate, formatEditorialDate } from '../utils/dates';
import { formatINR } from '../utils/format';

export default function HotelDetailPage() {
  const { id } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const today = getTodayString();
  const initialCheckIn = searchParams.get('checkIn') || addDays(today, 3);
  const initialCheckOut = searchParams.get('checkOut') || addDays(today, 6);

  const [hotel, setHotel] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Dates state
  const [dates, setDates] = useState({
    checkIn: initialCheckIn,
    checkOut: initialCheckOut,
  });

  // Live quote state
  const [quote, setQuote] = useState(null);
  const [quoteLoading, setQuoteLoading] = useState(false);
  const [quoteError, setQuoteError] = useState(null);
  const [nearestDates, setNearestDates] = useState([]);

  useEffect(() => {
    loadHotelDetail(dates.checkIn, dates.checkOut);
  }, [id]);

  useEffect(() => {
    if (dates.checkIn && dates.checkOut && dates.checkIn < dates.checkOut) {
      loadQuote(dates.checkIn, dates.checkOut);
    }
  }, [dates.checkIn, dates.checkOut]);

  const loadHotelDetail = async (cIn, cOut) => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchHotelById(id, { checkIn: cIn, checkOut: cOut });
      setHotel(data);
    } catch (err) {
      setError(err.message || 'Unable to retrieve property record.');
    } finally {
      setLoading(false);
    }
  };

  const loadQuote = async (cIn, cOut) => {
    setQuoteLoading(true);
    setQuoteError(null);
    setNearestDates([]);
    try {
      const quoteData = await fetchBookingQuote({
        hotelId: Number(id),
        checkIn: cIn,
        checkOut: cOut,
      });
      setQuote({
        ...quoteData,
        checkIn: cIn,
        checkOut: cOut,
      });
    } catch (err) {
      setQuoteError(err.message || 'No availability for selected dates.');
      if (err.nearestDates && err.nearestDates.length > 0) {
        setNearestDates(err.nearestDates);
      }
    } finally {
      setQuoteLoading(false);
    }
  };

  const handleDateChange = (newDates) => {
    setDates(newDates);
    if (newDates.checkIn && newDates.checkOut) {
      const p = new URLSearchParams(searchParams);
      p.set('checkIn', newDates.checkIn);
      p.set('checkOut', newDates.checkOut);
      setSearchParams(p);
      loadHotelDetail(newDates.checkIn, newDates.checkOut);
    }
  };

  const handleApplyAlternative = (alt) => {
    setDates({ checkIn: alt.checkIn, checkOut: alt.checkOut });
    const p = new URLSearchParams(searchParams);
    p.set('checkIn', alt.checkIn);
    p.set('checkOut', alt.checkOut);
    setSearchParams(p);
    loadHotelDetail(alt.checkIn, alt.checkOut);
    loadQuote(alt.checkIn, alt.checkOut);
  };

  const handleProceedToBooking = () => {
    const params = new URLSearchParams({
      hotelId: id,
      checkIn: dates.checkIn,
      checkOut: dates.checkOut,
    });
    navigate(`/book?${params.toString()}`);
  };

  const nights = calculateNights(dates.checkIn, dates.checkOut);
  const amenitiesList = hotel?.amenities ? hotel.amenities.split(',').map((s) => s.trim()) : [];

  if (loading) {
    return (
      <div className="hotel-detail-container">
        <Skeleton width="180px" height="14px" style={{ marginBottom: '16px' }} />
        <Skeleton width="60%" height="48px" style={{ marginBottom: '32px' }} />
        <div className="detail-layout">
          <div className="detail-left">
            <Skeleton width="100%" height="320px" style={{ marginBottom: '24px' }} />
            <Skeleton width="100%" height="100px" />
          </div>
          <div className="detail-right">
            <Skeleton width="100%" height="450px" />
          </div>
        </div>
      </div>
    );
  }

  if (error || !hotel) {
    return (
      <div className="hotel-detail-container">
        <EmptyState
          title="Property Unavailable"
          description={error || 'The requested property could not be located in our registry.'}
          action={
            <Link to="/hotels">
              <Button variant="secondary">Return to All Properties</Button>
            </Link>
          }
        />
      </div>
    );
  }

  const isSoldOut = hotel.availableRooms === 0 || !!quoteError;

  return (
    <div className="hotel-detail-container">
      {/* Back breadcrumb */}
      <nav className="detail-nav">
        <Link to="/hotels" className="back-link">
          <ArrowLeftIcon size={14} />
          <span>Back to All Hotels</span>
        </Link>
      </nav>

      {/* Hero Header */}
      <header className="detail-header">
        <div className="detail-meta">
          <span className="detail-city-badge mono">📍 {hotel.location.toUpperCase()}, INDIA</span>
          <span className="detail-inventory-badge mono">
            {hotel.availableRooms <= 2 ? (
              <span className="last-room-warning">🔥 ONLY {hotel.availableRooms} ROOMS LEFT</span>
            ) : (
              `${hotel.availableRooms} rooms available`
            )}
          </span>
        </div>
        <h1 className="detail-title serif-heading">{hotel.name}</h1>
      </header>

      {/* Asymmetric Two-Column Grid (60% Left, 40% Right) */}
      <div className="detail-layout">
        {/* Left Column: Story, Visual, Amenities, Nightly Availability */}
        <section className="detail-left">
          {/* Real Hotel Photography Banner */}
          <div className="detail-visual-wrapper">
            <HotelVisual
              id={hotel.id}
              name={hotel.name}
              location={hotel.location}
              imageUrl={hotel.imageUrl}
              height="340px"
              alt={hotel.name}
            />
          </div>

          <article className="detail-story">
            <h2 className="story-heading serif-heading">About this Hotel</h2>
            <p className="story-paragraph">{hotel.description}</p>
          </article>

          {/* Amenities tags */}
          <div className="detail-amenities-section">
            <h3 className="section-subtitle mono">HOTEL AMENITIES & HIGHLIGHTS</h3>
            <ul className="amenities-tag-cloud" aria-label="Hotel amenities">
              {amenitiesList.map((amenity, i) => (
                <li key={i} className="amenity-tag">
                  <CheckIcon size={13} />
                  <span>{amenity}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Per-Night Inventory Calendar Breakdown */}
          {hotel.roomAvailability && hotel.roomAvailability.length > 0 && (
            <div className="detail-inventory-section">
              <h3 className="section-subtitle mono">NIGHTLY ROOM AVAILABILITY</h3>
              <div className="inventory-timeline">
                {hotel.roomAvailability.map((slot) => (
                  <div
                    key={slot.date}
                    className={`timeline-slot ${slot.available <= 0 ? 'sold-out' : slot.available <= 2 ? 'scarce' : ''}`}
                  >
                    <span className="slot-date mono">{formatShortDate(slot.date)}</span>
                    <span className="slot-count mono">
                      {slot.available <= 0 ? 'FULL' : `${slot.available} free`}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </section>

        {/* Right Column: Sticky Ticket Booking Summary + Date Range Selector */}
        <aside className="detail-right">
          <div className="sticky-ticket-container">
            {/* Embedded Calendar Widget */}
            <div className="detail-calendar-block">
              <div className="cal-block-label">
                <span className="mono">SELECT DESIRED STAY DATES</span>
                <span className="mono nights-caption">{nights} NIGHTS</span>
              </div>
              <DateRangePicker
                checkIn={dates.checkIn}
                checkOut={dates.checkOut}
                onChange={handleDateChange}
              />
            </div>

            {/* Live Folio Ticket */}
            <div className="detail-ticket-slot">
              <Ticket
                quote={quote}
                hotel={hotel}
                showActions={false}
              />
            </div>

            {/* Nearest Available Dates Alert */}
            {quoteError && (
              <div className="nearest-dates-card">
                <div className="nd-header">
                  <span className="nd-icon">⚠️</span>
                  <div>
                    <h4 className="nd-title serif-heading">No Vacancy on Selected Nights</h4>
                    <p className="nd-subtitle">
                      Every room is occupied for this stay window. Consider these {nights}-night alternatives:
                    </p>
                  </div>
                </div>

                {nearestDates && nearestDates.length > 0 ? (
                  <div className="nd-stubs-list">
                    {nearestDates.map((alt, idx) => (
                      <button
                        key={idx}
                        type="button"
                        className="nd-ticket-stub"
                        onClick={() => handleApplyAlternative(alt)}
                      >
                        <div className="stub-dates">
                          <span className="stub-range mono">
                            {formatShortDate(alt.checkIn)} → {formatShortDate(alt.checkOut)}
                          </span>
                          <span className="stub-tag mono">{alt.nights} nights</span>
                        </div>
                        <div className="stub-price mono">
                          ₹{formatINR(alt.totalAmount)}
                          <span className="stub-select-hint">Select →</span>
                        </div>
                      </button>
                    ))}
                  </div>
                ) : (
                  <p className="nd-no-alternatives mono">
                    No matching vacancies within a 14-day window. Please select new dates.
                  </p>
                )}
              </div>
            )}

            {/* Confirm / Book Room CTA */}
            <div className="detail-booking-cta">
              <Button
                variant="primary"
                size="lg"
                disabled={!dates.checkIn || !dates.checkOut || nights <= 0 || !!quoteError || quoteLoading}
                loading={quoteLoading}
                onClick={handleProceedToBooking}
                icon={<ArrowRightIcon size={18} />}
                className="full-width-btn"
              >
                {quoteError ? 'Select Alternative Dates' : `Reserve Room (₹${formatINR(quote?.totalAmount || hotel.pricePerNight * nights)})`}
              </Button>
              <p className="cta-disclaimer mono">
                NO PAYMENT COLLECTED NOW · INSTANT DIGITAL CONFIRMATION
              </p>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
}

