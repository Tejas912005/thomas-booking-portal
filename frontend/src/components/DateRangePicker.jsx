import React, { useState } from 'react';
import './DateRangePicker.css';
import { ArrowLeftIcon, ArrowRightIcon, CalendarIcon, CheckIcon } from './Icons';
import { getTodayString, calculateNights, addDays, formatEditorialDate } from '../utils/dates';

export default function DateRangePicker({ checkIn, checkOut, onChange, onClose }) {
  const today = getTodayString();

  // Active picking step: 'checkIn' or 'checkOut'
  const [pickingStep, setPickingStep] = useState(checkIn && !checkOut ? 'checkOut' : 'checkIn');

  // Month 1 view date
  const [viewDate, setViewDate] = useState(() => {
    if (checkIn) {
      const [y, m] = checkIn.split('-').map(Number);
      return new Date(y, m - 1, 1);
    }
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth(), 1);
  });

  const year1 = viewDate.getFullYear();
  const month1 = viewDate.getMonth();

  // Month 2 is month1 + 1
  const month2Date = new Date(year1, month1 + 1, 1);
  const year2 = month2Date.getFullYear();
  const month2 = month2Date.getMonth();

  const handlePrevMonth = () => {
    setViewDate(new Date(year1, month1 - 1, 1));
  };

  const handleNextMonth = () => {
    setViewDate(new Date(year1, month1 + 1, 1));
  };

  // Helper to build day cells for a given year & month
  const buildMonthDays = (yr, mo) => {
    const firstDayOfWeek = new Date(yr, mo, 1).getDay(); // 0 is Sunday
    const daysInMonth = new Date(yr, mo + 1, 0).getDate();
    const days = [];
    for (let i = 0; i < firstDayOfWeek; i++) {
      days.push(null);
    }
    for (let d = 1; d <= daysInMonth; d++) {
      const mm = String(mo + 1).padStart(2, '0');
      const dd = String(d).padStart(2, '0');
      days.push(`${yr}-${mm}-${dd}`);
    }
    return days;
  };

  const daysMonth1 = buildMonthDays(year1, month1);
  const daysMonth2 = buildMonthDays(year2, month2);

  const handleDateClick = (dateStr) => {
    if (dateStr < today) return;

    if (pickingStep === 'checkIn') {
      // User is picking Check-In
      onChange({ checkIn: dateStr, checkOut: '' });
      setPickingStep('checkOut');
    } else {
      // User is picking Check-Out
      if (dateStr <= checkIn) {
        // If they click on or before current checkIn, treat as new checkIn
        onChange({ checkIn: dateStr, checkOut: '' });
        setPickingStep('checkOut');
      } else {
        onChange({ checkIn, checkOut: dateStr });
        setPickingStep('checkIn');
      }
    }
  };

  const handleQuickDuration = (numNights) => {
    const baseCheckIn = checkIn || today;
    const computedCheckOut = addDays(baseCheckIn, numNights);
    onChange({ checkIn: baseCheckIn, checkOut: computedCheckOut });
  };

  const handleClear = () => {
    onChange({ checkIn: '', checkOut: '' });
    setPickingStep('checkIn');
  };

  const nights = calculateNights(checkIn, checkOut);
  const weekDayHeaders = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];

  const renderMonthGrid = (yr, mo, daysList) => {
    const monthTitle = new Date(yr, mo, 1).toLocaleDateString('en-US', {
      month: 'long',
      year: 'numeric',
    });

    return (
      <div className="cal-single-month">
        <div className="cal-month-title">{monthTitle}</div>

        <div className="cal-weekdays">
          {weekDayHeaders.map((w, i) => (
            <span key={i} className="cal-weekday">{w}</span>
          ))}
        </div>

        <div className="cal-grid">
          {daysList.map((dateStr, idx) => {
            if (!dateStr) {
              return <div key={`empty-${idx}`} className="cal-cell empty" />;
            }

            const isPast = dateStr < today;
            const isCheckIn = dateStr === checkIn;
            const isCheckOut = dateStr === checkOut;
            const isInRange = checkIn && checkOut && dateStr > checkIn && dateStr < checkOut;
            const isToday = dateStr === today;

            let cellClass = 'cal-cell';
            if (isPast) cellClass += ' is-past';
            if (isCheckIn) cellClass += ' is-checkin';
            if (isCheckOut) cellClass += ' is-checkout';
            if (isInRange) cellClass += ' is-in-range';
            if (isToday) cellClass += ' is-today';

            const dayNumber = parseInt(dateStr.slice(8), 10);

            return (
              <button
                key={dateStr}
                type="button"
                disabled={isPast}
                onClick={() => handleDateClick(dateStr)}
                className={cellClass}
                title={dateStr}
              >
                <span className="cal-day-num">{dayNumber}</span>
                {isCheckIn && <span className="cal-mini-badge">IN</span>}
                {isCheckOut && <span className="cal-mini-badge">OUT</span>}
              </button>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <div className="dual-calendar-card">
      {/* ── Top Bar: Step Selector & Instruction ── */}
      <div className="dual-cal-header-bar">
        <div className="cal-step-tabs">
          <button
            type="button"
            className={`cal-tab-btn ${pickingStep === 'checkIn' ? 'active' : ''}`}
            onClick={() => setPickingStep('checkIn')}
          >
            <span className="tab-eyebrow">1. CHECK-IN</span>
            <span className="tab-date-val">
              {checkIn ? formatEditorialDate(checkIn) : 'Choose Date'}
            </span>
          </button>

          <span className="cal-tab-arrow">→</span>

          <button
            type="button"
            className={`cal-tab-btn ${pickingStep === 'checkOut' ? 'active' : ''}`}
            onClick={() => setPickingStep('checkOut')}
          >
            <span className="tab-eyebrow">2. CHECK-OUT</span>
            <span className="tab-date-val">
              {checkOut ? formatEditorialDate(checkOut) : 'Choose Date'}
            </span>
          </button>
        </div>

        <div className="cal-instruction-box">
          {pickingStep === 'checkIn' ? (
            <span className="instruction-text">
              👉 Click on a date to set your <strong>Check-In</strong>
            </span>
          ) : (
            <span className="instruction-text highlight">
              👉 Now click your departure date for <strong>Check-Out</strong>
            </span>
          )}
        </div>
      </div>

      {/* ── Month Navigation Controls ── */}
      <div className="cal-months-nav-row">
        <button
          type="button"
          onClick={handlePrevMonth}
          className="cal-nav-arrow-btn"
          aria-label="Previous month"
        >
          <ArrowLeftIcon size={16} />
          <span>Previous</span>
        </button>

        <span className="cal-nav-center-hint">
          {nights > 0 ? `${nights} nights selected` : 'Select stay dates'}
        </span>

        <button
          type="button"
          onClick={handleNextMonth}
          className="cal-nav-arrow-btn"
          aria-label="Next month"
        >
          <span>Next</span>
          <ArrowRightIcon size={16} />
        </button>
      </div>

      {/* ── Two Side-by-Side Calendar Months ── */}
      <div className="dual-months-wrapper">
        {renderMonthGrid(year1, month1, daysMonth1)}
        {renderMonthGrid(year2, month2, daysMonth2)}
      </div>

      {/* ── Quick Duration Shortcuts ── */}
      <div className="cal-quick-durations">
        <span className="quick-label">QUICK SELECT:</span>
        <button type="button" onClick={() => handleQuickDuration(1)} className="quick-duration-btn">
          1 Night
        </button>
        <button type="button" onClick={() => handleQuickDuration(2)} className="quick-duration-btn">
          2 Nights
        </button>
        <button type="button" onClick={() => handleQuickDuration(3)} className="quick-duration-btn">
          3 Nights
        </button>
        <button type="button" onClick={() => handleQuickDuration(5)} className="quick-duration-btn">
          5 Nights
        </button>
        <button type="button" onClick={() => handleQuickDuration(7)} className="quick-duration-btn">
          1 Week
        </button>
      </div>

      {/* ── Bottom Action Footer ── */}
      <div className="cal-bottom-footer">
        <div className="cal-summary-pill">
          {checkIn && checkOut ? (
            <span>
              <strong>{nights} {nights === 1 ? 'Night' : 'Nights'}</strong> ({formatEditorialDate(checkIn)} to {formatEditorialDate(checkOut)})
            </span>
          ) : checkIn ? (
            <span>Check-In selected: <strong>{formatEditorialDate(checkIn)}</strong>. Please pick Check-Out.</span>
          ) : (
            <span>Please select your arrival date</span>
          )}
        </div>

        <div className="cal-footer-actions">
          <button type="button" onClick={handleClear} className="cal-clear-btn">
            Clear Dates
          </button>
          {onClose && (
            <button type="button" onClick={onClose} className="cal-done-btn">
              Apply Dates
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
