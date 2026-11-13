import React, { useState } from 'react';
import './Ticket.css';
import { CopyIcon, PrinterIcon, CheckIcon } from './Icons';
import { formatINR, formatReference } from '../utils/format';
import { formatEditorialDate, calculateNights } from '../utils/dates';

// Decorative SVG Barcode for authentic Boarding Pass look
function BoardingBarcode({ code = 'HB-000000', className = '' }) {
  // Generate deterministic bar widths from code characters
  const bars = [];
  let x = 10;
  for (let i = 0; i < code.length; i++) {
    const val = code.charCodeAt(i);
    const w1 = (val % 3) + 1.5;
    const w2 = ((val >> 1) % 2) + 1;
    const w3 = ((val >> 2) % 3) + 1.5;
    bars.push({ x, width: w1 });
    x += w1 + 2;
    bars.push({ x, width: w2 });
    x += w2 + 1.5;
    bars.push({ x, width: w3 });
    x += w3 + 2.5;
  }
  // Trailing guard bars
  bars.push({ x, width: 2 });
  x += 4;
  bars.push({ x, width: 3 });
  x += 5;
  bars.push({ x, width: 1.5 });
  x += 10;

  return (
    <div className={`boarding-barcode-wrapper ${className}`}>
      <svg
        viewBox={`0 0 ${x} 48`}
        width="100%"
        height="48"
        preserveAspectRatio="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {bars.map((b, idx) => (
          <rect key={idx} x={b.x} y="0" width={b.width} height="48" fill="currentColor" />
        ))}
      </svg>
      <span className="barcode-caption mono">*{code}*</span>
    </div>
  );
}

export default function Ticket({
  booking,
  quote,
  hotel,
  onCancel,
  showActions = true,
  isFullPass = false,
  className = '',
}) {
  const [copied, setCopied] = useState(false);

  const reference = booking?.reference || quote?.reference || 'HB-SAMPLE';
  const guestName = booking?.guestName || quote?.guestName || 'Guest of Record';
  const guestEmail = booking?.guestEmail || quote?.guestEmail || '';
  const checkIn = booking?.checkIn || quote?.checkIn || '';
  const checkOut = booking?.checkOut || quote?.checkOut || '';
  const nights = booking?.nights || quote?.nights || (checkIn && checkOut ? calculateNights(checkIn, checkOut) : 1);
  const totalAmount = booking?.totalAmount || quote?.totalAmount || 0;
  
  // Robust pricePerNight calculation: never 0 when total and nights are present
  const pricePerNight =
    booking?.pricePerNight ||
    booking?.hotel?.pricePerNight ||
    hotel?.pricePerNight ||
    quote?.pricePerNight ||
    (nights > 0 && totalAmount ? totalAmount / nights : 3500);

  const status = booking?.status || 'PREVIEW';
  const hotelName = booking?.hotelName || hotel?.name || 'The Taj Mahal Palace';
  const hotelLocation = booking?.hotelLocation || hotel?.location || 'Mumbai';

  const handleCopy = () => {
    if (!reference) return;
    navigator.clipboard.writeText(reference).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <article className={`ticket-pass ${isFullPass ? 'ticket-boarding-pass' : 'ticket-sidebar'} ${className}`}>
      {/* ── Top Flight / Folio Header Bar ── */}
      <header className="ticket-header-strip">
        <div className="ticket-brand-cluster">
          <span className="ticket-edition-mono mono">HOTEL PASS · 2026</span>
          <span className="ticket-brand-title serif-heading">Grand Tour Hotels</span>
          <span className="ticket-brand-slug mono">OFFICIAL DIGITAL BOARDING PASS</span>
        </div>
        <div className="ticket-status-cluster">
          <span className={`ticket-status-pill mono ${status.toLowerCase()}`}>
            {status === 'CONFIRMED' && '● CONFIRMED RESERVATION'}
            {status === 'CANCELLED' && '✕ RESERVATION CANCELLED'}
            {status === 'PREVIEW' && '○ LIVE PRICE QUOTE'}
          </span>
          <div className="ticket-ref-display">
            <span className="ref-caption mono">BOOKING REFERENCE</span>
            <span className="ref-code mono">{formatReference(reference)}</span>
          </div>
        </div>
      </header>

      {/* ── Pass Content Layout: Main Pass Body + Tear-off Stub ── */}
      <div className="ticket-inner-grid">
        {/* Main Section */}
        <div className="ticket-main-section">
          {/* Passenger & Destination Row */}
          <div className="ticket-grid-row">
            <div className="ticket-cell cell-passenger">
              <span className="cell-label mono">PRIMARY GUEST</span>
              <h2 className="passenger-name serif-heading">{guestName}</h2>
              {guestEmail && <span className="passenger-email mono">{guestEmail}</span>}
            </div>

            <div className="ticket-cell cell-destination">
              <span className="cell-label mono">HOTEL / RESIDENCE</span>
              <h3 className="destination-name serif-heading">{hotelName}</h3>
              <span className="destination-city mono">{hotelLocation.toUpperCase()} · INDIA</span>
            </div>
          </div>

          {/* Stay Dates & Timings Row */}
          <div className="ticket-itinerary-box">
            <div className="itin-col">
              <span className="cell-label mono">CHECK-IN</span>
              <span className="itin-date serif-heading">{formatEditorialDate(checkIn) || '—'}</span>
              <span className="itin-time mono">FROM 14:00 HRS</span>
            </div>

            <div className="itin-duration">
              <span className="itin-nights-badge mono">{nights} NIGHTS</span>
              <div className="itin-track-line" aria-hidden="true" />
            </div>

            <div className="itin-col">
              <span className="cell-label mono">CHECK-OUT</span>
              <span className="itin-date serif-heading">{formatEditorialDate(checkOut) || '—'}</span>
              <span className="itin-time mono">UNTIL 11:00 HRS</span>
            </div>
          </div>

          {/* Secondary Details Row (Class, Room, Policy) */}
          <div className="ticket-meta-grid">
            <div className="meta-item">
              <span className="cell-label mono">ACCOMMODATION CLASS</span>
              <span className="meta-val mono">DELUXE HERITAGE SUITE</span>
            </div>
            <div className="meta-item">
              <span className="cell-label mono">KEY RELEASE</span>
              <span className="meta-val mono">AT MAIN FOYER CONCIERGE</span>
            </div>
            <div className="meta-item">
              <span className="cell-label mono">INVENTORY LOCK</span>
              <span className="meta-val mono">PESSIMISTIC SECURED</span>
            </div>
          </div>
        </div>

        {/* ── Perforation Tear Line with Cutout Notches ── */}
        <div className="ticket-perforation-divider" aria-hidden="true">
          <div className="perforated-notch notch-top" />
          <div className="perforated-line" />
          <div className="perforated-notch notch-bottom" />
        </div>

        {/* ── Right / Financial Tear-off Stub ── */}
        <aside className="ticket-stub-section">
          <div className="stub-header">
            <span className="stub-label mono">FOLIO BREAKDOWN</span>
            <span className="stub-ref mono">{formatReference(reference)}</span>
          </div>

          <div className="stub-pricing-table">
            <div className="stub-price-row">
              <span className="stub-desc mono">{nights} nights × ₹{formatINR(pricePerNight)}</span>
              <span className="stub-val mono">₹{formatINR(totalAmount)}</span>
            </div>
            <div className="stub-price-row">
              <span className="stub-desc mono">Hospitality Tax & Dues</span>
              <span className="stub-val mono">INCLUDED</span>
            </div>
          </div>

          <div className="stub-total-card">
            <div className="stub-total-header">
              <span className="stub-total-title mono">TOTAL FOLIO PAYABLE</span>
              <span className="stub-currency mono">INR (₹)</span>
            </div>
            <div className="stub-total-amount mono">
              ₹{formatINR(totalAmount)}
            </div>
          </div>

          {/* Authentic Boarding Barcode */}
          <div className="stub-barcode-slot">
            <BoardingBarcode code={reference} />
          </div>

          {/* Screen-only action buttons */}
          {showActions && (
            <div className="stub-actions no-print">
              {reference && (
                <button
                  type="button"
                  className="stub-btn-action"
                  onClick={handleCopy}
                  title="Copy reference code"
                >
                  {copied ? <CheckIcon size={14} /> : <CopyIcon size={14} />}
                  <span>{copied ? 'Copied' : 'Copy'}</span>
                </button>
              )}
              {reference && (
                <button
                  type="button"
                  className="stub-btn-action print-btn"
                  onClick={handlePrint}
                  title="Print Boarding Pass (1 Page)"
                >
                  <PrinterIcon size={14} />
                  <span>Print Pass</span>
                </button>
              )}
              {onCancel && status === 'CONFIRMED' && (
                <button
                  type="button"
                  className="stub-btn-cancel"
                  onClick={onCancel}
                >
                  Cancel
                </button>
              )}
            </div>
          )}
        </aside>
      </div>

      {/* Security fine print */}
      <footer className="ticket-footer-strip mono">
        <span>SECURITY TOKEN: {reference}-{nights}N · ISSUED UNDER AUTUMN MMXXVI ALLOCATION</span>
        <span className="print-only-stamp">PRESENT THIS BOARDING PASS AT PROPERTY CONCIERGE</span>
      </footer>
    </article>
  );
}
