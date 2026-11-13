import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import './BookingPage.css';
import { fetchHotelById } from '../api/hotels';
import { fetchBookingQuote, createBooking } from '../api/bookings';
import Field from '../components/Field';
import Button from '../components/Button';
import Ticket from '../components/Ticket';
import Skeleton from '../components/Skeleton';
import EmptyState from '../components/EmptyState';
import { ArrowLeftIcon, CheckIcon } from '../components/Icons';
import { formatEditorialDate, calculateNights } from '../utils/dates';
import { formatINR } from '../utils/format';

export default function BookingPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const hotelId = searchParams.get('hotelId');
  const checkIn = searchParams.get('checkIn');
  const checkOut = searchParams.get('checkOut');

  const [hotel, setHotel] = useState(null);
  const [quote, setQuote] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [generalError, setGeneralError] = useState(null);

  // Form state
  const [guestName, setGuestName] = useState('');
  const [guestEmail, setGuestEmail] = useState('');
  const [guestPhone, setGuestPhone] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

  useEffect(() => {
    if (!hotelId || !checkIn || !checkOut) {
      setLoading(false);
      return;
    }
    loadData();
  }, [hotelId, checkIn, checkOut]);

  const loadData = async () => {
    setLoading(true);
    setGeneralError(null);
    try {
      const [hotelData, quoteData] = await Promise.all([
        fetchHotelById(hotelId, { checkIn, checkOut }),
        fetchBookingQuote({ hotelId: Number(hotelId), checkIn, checkOut }),
      ]);
      setHotel(hotelData);
      setQuote({
        ...quoteData,
        checkIn,
        checkOut,
      });
    } catch (err) {
      setGeneralError(err.message || 'Unable to prepare booking quote.');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};
    if (!guestName.trim()) {
      errors.guestName = 'Please enter the principal guest name.';
    } else if (guestName.length > 200) {
      errors.guestName = 'Guest name cannot exceed 200 characters.';
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!guestEmail.trim()) {
      errors.guestEmail = 'Please provide an email for booking dispatch.';
    } else if (!emailRegex.test(guestEmail.trim())) {
      errors.guestEmail = 'Please enter a valid electronic mail address.';
    } else if (guestEmail.length > 255) {
      errors.guestEmail = 'Email address is too long.';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGeneralError(null);

    if (!validateForm()) return;

    setSubmitting(true);
    try {
      const booking = await createBooking({
        hotelId: Number(hotelId),
        guestName: guestName.trim(),
        guestEmail: guestEmail.trim(),
        checkIn,
        checkOut,
      });

      // Navigate to confirmation page
      const phoneParam = guestPhone ? `&phone=${encodeURIComponent(guestPhone.trim())}` : '';
      navigate(`/confirmation?reference=${encodeURIComponent(booking.reference)}&email=${encodeURIComponent(booking.guestEmail)}${phoneParam}`);
    } catch (err) {
      setSubmitting(false);
      // Map server field errors if available
      if (err.fieldErrors && err.fieldErrors.length > 0) {
        const serverErrors = {};
        err.fieldErrors.forEach((fe) => {
          serverErrors[fe.field] = fe.message;
        });
        setFieldErrors(serverErrors);
      }
      setGeneralError(err.message || 'Unable to confirm reservation. Please try again.');
    }
  };

  if (!hotelId || !checkIn || !checkOut) {
    return (
      <div className="booking-page-container">
        <EmptyState
          title="Incomplete Reservation Request"
          description="Please select a destination sanctuary and your desired stay dates before booking."
          action={
            <Link to="/hotels">
              <Button variant="primary">Explore Available Residences</Button>
            </Link>
          }
        />
      </div>
    );
  }

  if (loading) {
    return (
      <div className="booking-page-container">
        <Skeleton width="180px" height="14px" style={{ marginBottom: '16px' }} />
        <Skeleton width="40%" height="36px" style={{ marginBottom: '32px' }} />
        <div className="booking-grid">
          <Skeleton width="100%" height="320px" />
          <Skeleton width="100%" height="320px" />
        </div>
      </div>
    );
  }

  const nights = calculateNights(checkIn, checkOut);

  return (
    <div className="booking-page-container">
      <nav className="booking-nav">
        <Link to={`/hotels/${hotelId}?checkIn=${checkIn}&checkOut=${checkOut}`} className="back-link">
          <ArrowLeftIcon size={14} />
          <span>Return to Property Details</span>
        </Link>
      </nav>

      <header className="booking-header">
        <span className="booking-eyebrow mono">RESERVATION DETAILS</span>
        <h1 className="booking-title serif-heading">Confirm Your Stay</h1>
      </header>

      {generalError && (
        <div className="booking-error-banner" role="alert">
          <span className="error-icon">⚠️</span>
          <div>
            <h4 className="error-title">Reservation Could Not Be Completed</h4>
            <p className="error-msg">{generalError}</p>
          </div>
        </div>
      )}

      <div className="booking-grid">
        {/* Left Column: Guest Intake Form */}
        <section className="booking-form-col">
          <form onSubmit={handleSubmit} noValidate className="editorial-booking-form">
            <div className="form-section-header">
              <span className="form-step-num mono">STEP 1</span>
              <h2 className="form-step-title serif-heading">Guest Information</h2>
              <p className="form-step-desc">
                Your booking confirmation and digital boarding pass will be sent to this email.
              </p>
            </div>

            <div className="form-fields">
              <Field
                id="guestName"
                label="Full Name"
                placeholder="e.g. Rahul Sharma"
                value={guestName}
                onChange={(e) => {
                  setGuestName(e.target.value);
                  if (fieldErrors.guestName) {
                    setFieldErrors((prev) => ({ ...prev, guestName: '' }));
                  }
                }}
                error={fieldErrors.guestName}
                required
                disabled={submitting}
              />

              <Field
                id="guestEmail"
                label="Email Address (for Real Booking Pass Email)"
                type="email"
                placeholder="e.g. yourname@gmail.com"
                value={guestEmail}
                onChange={(e) => {
                  setGuestEmail(e.target.value);
                  if (fieldErrors.guestEmail) {
                    setFieldErrors((prev) => ({ ...prev, guestEmail: '' }));
                  }
                }}
                error={fieldErrors.guestEmail}
                hint="Your official digital booking confirmation pass will be sent here."
                required
                disabled={submitting}
              />

              <Field
                id="guestPhone"
                label="WhatsApp Mobile Number"
                type="tel"
                placeholder="e.g. +91 98765 43210"
                value={guestPhone}
                onChange={(e) => setGuestPhone(e.target.value)}
                hint="For instant 1-tap WhatsApp boarding pass delivery."
                disabled={submitting}
              />
            </div>

            <div className="booking-commit-box">
              <Button
                type="submit"
                variant="primary"
                size="lg"
                loading={submitting}
                disabled={submitting}
                className="commit-btn"
              >
                Confirm Reservation Folio →
              </Button>
              <p className="commit-terms mono">
                BY CONFIRMING, YOU AUTHORISE A PESSIMISTIC INVENTORY LOCK ON ROOM #{hotelId}.
              </p>
            </div>
          </form>
        </section>

        {/* Right Column: Verified Quote Folio */}
        <aside className="booking-summary-col">
          <div className="summary-wrapper">
            <span className="summary-label mono">VERIFIED BACKEND QUOTE</span>
            <Ticket
              quote={quote}
              hotel={hotel}
              showActions={false}
            />

            <div className="policy-note">
              <h4 className="policy-title serif-heading">Flexible Cancellation Policy</h4>
              <p className="policy-text">
                Reservations may be cancelled at any time prior to arrival via your reference code and email. Inventory is immediately released back to the general pool.
              </p>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
}

