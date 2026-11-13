import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import './ManageBookingPage.css';
import { lookupBooking, cancelBooking } from '../api/bookings';
import Field from '../components/Field';
import Button from '../components/Button';
import Ticket from '../components/Ticket';
import Toast from '../components/Toast';
import EmptyState from '../components/EmptyState';
import { SearchIcon, CrossIcon } from '../components/Icons';

export default function ManageBookingPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialRef = searchParams.get('reference') || '';
  const initialEmail = searchParams.get('email') || '';

  const [reference, setReference] = useState(initialRef);
  const [email, setEmail] = useState(initialEmail);
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState(null);

  // Cancellation modal state
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    if (initialRef && initialEmail) {
      handleLookup(initialRef, initialEmail);
    }
  }, [initialRef, initialEmail]);

  const handleLookup = async (refVal, emailVal) => {
    setError(null);
    setLoading(true);
    try {
      const data = await lookupBooking({
        reference: refVal.trim().toUpperCase(),
        email: emailVal.trim(),
      });
      setBooking(data);
      const newParams = new URLSearchParams();
      newParams.set('reference', refVal.trim().toUpperCase());
      newParams.set('email', emailVal.trim());
      setSearchParams(newParams);
    } catch (err) {
      setBooking(null);
      setError(err.message || 'No reservation matches that reference and email combination.');
    } finally {
      setLoading(false);
    }
  };

  const onSearchSubmit = (e) => {
    e.preventDefault();
    if (!reference.trim() || !email.trim()) {
      setError('Please provide both the booking reference and email.');
      return;
    }
    handleLookup(reference, email);
  };

  const handleConfirmCancel = async () => {
    setCancelling(true);
    try {
      const updated = await cancelBooking(booking.reference, email.trim());
      setBooking(updated);
      setShowCancelDialog(false);
      setToast('Reservation cancelled and inventory restored.');
    } catch (err) {
      setShowCancelDialog(false);
      setError(err.message || 'Unable to cancel this reservation.');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="manage-container">
      {toast && (
        <Toast
          message={toast}
          type="success"
          onClose={() => setToast(null)}
        />
      )}

      <header className="manage-header">
        <span className="manage-eyebrow mono">FIND MY RESERVATION</span>
        <h1 className="manage-title serif-heading">Manage Your Booking</h1>
        <p className="manage-subtitle">
          View your hotel boarding pass, review stay details, or cancel your reservation anytime.
        </p>
      </header>

      {/* Two-field Lookup Form */}
      <section className="manage-search-section">
        <form onSubmit={onSearchSubmit} className="manage-search-form">
          <div className="manage-fields-row">
            <Field
              id="refInput"
              label="Booking Reference"
              placeholder="e.g. HB-AB12CD"
              value={reference}
              onChange={(e) => setReference(e.target.value.toUpperCase())}
              mono
              required
            />
            <Field
              id="emailInput"
              label="Guest Email Address"
              type="email"
              placeholder="e.g. yourname@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="manage-form-action">
            <Button
              type="submit"
              variant="primary"
              size="md"
              loading={loading}
              icon={<SearchIcon size={16} />}
            >
              Locate Reservation
            </Button>
          </div>
        </form>
      </section>

      {/* Error display */}
      {error && (
        <div className="manage-error-alert" role="alert">
          <span className="alert-icon">⚠️</span>
          <span>{error}</span>
        </div>
      )}

      {/* Booking Display */}
      {booking && (
        <main className="manage-result-wrapper">
          <div className="manage-ticket-meta">
            <span className="mono">RECORD STATUS: {booking.status}</span>
            {booking.cancelledAt && (
              <span className="mono cancel-timestamp">
                RELEASED AT: {new Date(booking.cancelledAt).toLocaleString()}
              </span>
            )}
          </div>

          <Ticket
            booking={booking}
            isFullPass={true}
            showActions={true}
            onCancel={() => setShowCancelDialog(true)}
          />
        </main>
      )}

      {/* Minimal Confirm Cancellation Modal Dialog */}
      {showCancelDialog && (
        <div className="modal-backdrop" role="dialog" aria-modal="true">
          <div className="modal-dialog">
            <div className="modal-header">
              <span className="modal-tag mono">IRREVERSIBLE ACTION</span>
              <h3 className="modal-title serif-heading">Relinquish Room Booking?</h3>
            </div>
            <p className="modal-body">
              Are you certain you wish to cancel reservation <strong>{booking.reference}</strong> at <strong>{booking.hotelName}</strong>? Your room inventory will immediately be returned to the open reservation pool.
            </p>
            <div className="modal-footer">
              <Button
                variant="ghost"
                onClick={() => setShowCancelDialog(false)}
                disabled={cancelling}
              >
                Keep Reservation
              </Button>
              <Button
                variant="danger"
                onClick={handleConfirmCancel}
                loading={cancelling}
              >
                Yes, Cancel Reservation
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

