import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import './ConfirmationPage.css';
import { lookupBooking } from '../api/bookings';
import Ticket from '../components/Ticket';
import Button from '../components/Button';
import Skeleton from '../components/Skeleton';
import EmptyState from '../components/EmptyState';
import Toast from '../components/Toast';
import { CheckIcon, ArrowRightIcon } from '../components/Icons';
import { formatINR } from '../utils/format';

export default function ConfirmationPage() {
  const [searchParams] = useSearchParams();
  const reference = searchParams.get('reference');
  const email = searchParams.get('email');
  const phone = searchParams.get('phone') || '';

  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    if (!reference || !email) {
      setLoading(false);
      return;
    }
    loadBooking();
  }, [reference, email]);

  const loadBooking = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await lookupBooking({ reference, email });
      setBooking(data);
      setToast('Booking confirmed & room locked.');
    } catch (err) {
      setError(err.message || 'Unable to retrieve reservation record.');
    } finally {
      setLoading(false);
    }
  };

  // 1-Tap Real WhatsApp Dispatch
  const handleSendWhatsApp = () => {
    if (!booking) return;
    const hotelName = booking.hotelName || booking.hotel?.name || 'Hotel';
    const hotelLoc = booking.hotelLocation || booking.hotel?.location || 'India';
    const cleanPhone = (phone || '').replace(/[^0-9]/g, '');
    const message = `🏨 *Grand Tour Hotels — Reservation Confirmed*\n\n` +
      `*Booking Ref:* ${booking.reference}\n` +
      `*Guest:* ${booking.guestName}\n` +
      `*Hotel:* ${hotelName}, ${hotelLoc}\n` +
      `*Dates:* ${booking.checkIn} to ${booking.checkOut} (${booking.nights} Nights)\n` +
      `*Total Paid:* ₹${formatINR(booking.totalAmount)}\n\n` +
      `*Digital Boarding Pass:* ${window.location.href}`;

    const waUrl = cleanPhone
      ? `https://wa.me/${cleanPhone}?text=${encodeURIComponent(message)}`
      : `https://wa.me/?text=${encodeURIComponent(message)}`;
    window.open(waUrl, '_blank');
  };

  // 1-Click Open in Gmail
  const handleOpenGmail = () => {
    if (!booking) return;
    const hotelName = booking.hotelName || booking.hotel?.name || 'Hotel';
    const subject = `Hotel Confirmation: ${booking.reference} — ${hotelName}`;
    const body = `Dear ${booking.guestName},\n\nYour stay is confirmed!\n\nBooking Reference: ${booking.reference}\nHotel: ${hotelName}\nCheck-In: ${booking.checkIn}\nCheck-Out: ${booking.checkOut}\nNights: ${booking.nights}\nTotal Amount: ₹${formatINR(booking.totalAmount)}\n\nBoarding Pass Link: ${window.location.href}\n\nThank you for choosing Grand Tour Hotels.`;
    const gmailUrl = `https://mail.google.com/mail/?view=cm&fs=1&to=${encodeURIComponent(booking.guestEmail)}&su=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    window.open(gmailUrl, '_blank');
  };

  // 1-Click Download Calendar (.ics)
  const handleAddToCalendar = () => {
    if (!booking) return;
    const hotelName = booking.hotelName || booking.hotel?.name || 'Hotel';
    const hotelLoc = booking.hotelLocation || booking.hotel?.location || 'India';
    const startDate = booking.checkIn.replace(/-/g, '');
    const endDate = booking.checkOut.replace(/-/g, '');
    const icsData = [
      'BEGIN:VCALENDAR',
      'VERSION:2.0',
      'PRODID:-//Grand Tour Hotels//Hotel Reservation//EN',
      'BEGIN:VEVENT',
      `SUMMARY:Stay at ${hotelName} (${booking.reference})`,
      `DESCRIPTION:Hotel Booking Reference: ${booking.reference}\\nGuest: ${booking.guestName}\\nTotal: ₹${formatINR(booking.totalAmount)}`,
      `LOCATION:${hotelName}, ${hotelLoc}, India`,
      `DTSTART;VALUE=DATE:${startDate}`,
      `DTEND;VALUE=DATE:${endDate}`,
      'STATUS:CONFIRMED',
      'END:VEVENT',
      'END:VCALENDAR'
    ].join('\r\n');

    const blob = new Blob([icsData], { type: 'text/calendar;charset=utf-8' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.setAttribute('download', `Stay_${booking.reference}.ics`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (!reference || !email) {
    return (
      <div className="confirmation-container">
        <EmptyState
          title="No Reference Specified"
          description="A booking reference number and guest email address are required to view this confirmation pass."
          action={
            <Link to="/manage">
              <Button variant="primary">Retrieve Existing Booking</Button>
            </Link>
          }
        />
      </div>
    );
  }

  if (loading) {
    return (
      <div className="confirmation-container">
        <Skeleton width="200px" height="16px" style={{ margin: '0 auto 16px' }} />
        <Skeleton width="50%" height="40px" style={{ margin: '0 auto 32px' }} />
        <Skeleton width="100%" height="450px" style={{ maxWidth: '680px', margin: '0 auto' }} />
      </div>
    );
  }

  if (error || !booking) {
    return (
      <div className="confirmation-container">
        <EmptyState
          title="Booking Not Found"
          description={error || 'We were unable to locate this reservation record.'}
          action={
            <Link to="/manage">
              <Button variant="secondary">Search Existing Bookings</Button>
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="confirmation-container">
      {toast && (
        <Toast
          message={toast}
          type="success"
          onClose={() => setToast(null)}
        />
      )}

      {/* Top Header */}
      <header className="confirmation-header no-print">
        <div className="conf-badge mono">
          <CheckIcon size={14} />
          <span>OFFICIAL HOTEL PASS ISSUED</span>
        </div>
        <h1 className="conf-title serif-heading">Reservation Confirmed</h1>
        <p className="conf-subtitle">
          Your room has been locked and confirmed. An official receipt has been sent to <strong>{booking.guestEmail}</strong>.
        </p>

        {/* ── Real Communication Dispatch Toolbar ── */}
        <div className="conf-dispatch-bar">
          <button
            type="button"
            onClick={handleSendWhatsApp}
            className="dispatch-btn btn-whatsapp"
            title="Open receipt in WhatsApp"
          >
            <span>💬 Send Pass to WhatsApp</span>
          </button>

          <button
            type="button"
            onClick={handleOpenGmail}
            className="dispatch-btn btn-gmail"
            title="Open in Gmail"
          >
            <span>✉️ Open in Gmail</span>
          </button>

          <button
            type="button"
            onClick={handleAddToCalendar}
            className="dispatch-btn btn-calendar"
            title="Add event to Calendar"
          >
            <span>📅 Add to Calendar (.ics)</span>
          </button>
        </div>
      </header>

      {/* Boarding Pass Ticket Component */}
      <main className="confirmation-pass-wrapper">
        <Ticket
          booking={booking}
          isFullPass={true}
          showActions={true}
        />
      </main>

      {/* Next Actions */}
      <footer className="confirmation-footer no-print">
        <div className="conf-links-group">
          <Link to={`/manage?reference=${encodeURIComponent(booking.reference)}&email=${encodeURIComponent(booking.guestEmail)}`} className="conf-action-link">
            Manage or Cancel this Reservation →
          </Link>
          <Link to="/hotels" className="conf-action-link">
            Explore More Hotels →
          </Link>
        </div>
      </footer>
    </div>
  );
}
