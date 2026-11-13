import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import './ResultsPage.css';
import { fetchHotels } from '../api/hotels';
import HotelRow from '../components/HotelRow';
import { HotelListSkeleton } from '../components/Skeleton';
import EmptyState from '../components/EmptyState';
import DateRangePicker from '../components/DateRangePicker';
import Button from '../components/Button';
import { CalendarIcon, SearchIcon, ArrowRightIcon } from '../components/Icons';
import { formatEditorialDate, calculateNights } from '../utils/dates';

export default function ResultsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const locationParam = searchParams.get('location') || '';
  const checkInParam = searchParams.get('checkIn') || '';
  const checkOutParam = searchParams.get('checkOut') || '';

  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters & sorting
  const [selectedCity, setSelectedCity] = useState(locationParam);
  const [sortBy, setSortBy] = useState('price-asc'); // 'price-asc', 'price-desc', 'name'
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [tempDates, setTempDates] = useState({
    checkIn: checkInParam,
    checkOut: checkOutParam,
  });

  const cities = ['All', 'Mumbai', 'Goa', 'Shimla', 'Bangalore', 'Jaisalmer', 'Coorg'];

  useEffect(() => {
    setSelectedCity(locationParam);
    setTempDates({ checkIn: checkInParam, checkOut: checkOutParam });
    loadHotels(locationParam, checkInParam, checkOutParam);
  }, [locationParam, checkInParam, checkOutParam]);

  const loadHotels = async (loc, cIn, cOut) => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchHotels({
        location: loc === 'All' ? '' : loc,
        checkIn: cIn,
        checkOut: cOut,
      });
      setHotels(data);
    } catch (err) {
      setError(err.message || 'Unable to load accommodations.');
    } finally {
      setLoading(false);
    }
  };

  const handleCitySelect = (city) => {
    setSelectedCity(city);
    const newParams = new URLSearchParams(searchParams);
    if (city === 'All' || !city) {
      newParams.delete('location');
    } else {
      newParams.set('location', city);
    }
    setSearchParams(newParams);
  };

  const handleApplyDates = () => {
    const newParams = new URLSearchParams(searchParams);
    if (tempDates.checkIn) newParams.set('checkIn', tempDates.checkIn);
    else newParams.delete('checkIn');
    if (tempDates.checkOut) newParams.set('checkOut', tempDates.checkOut);
    else newParams.delete('checkOut');
    setSearchParams(newParams);
    setShowDatePicker(false);
  };

  // Sort list
  const sortedHotels = [...hotels].sort((a, b) => {
    if (sortBy === 'price-asc') return a.pricePerNight - b.pricePerNight;
    if (sortBy === 'price-desc') return b.pricePerNight - a.pricePerNight;
    if (sortBy === 'name') return a.name.localeCompare(b.name);
    return 0;
  });

  const nights = calculateNights(checkInParam, checkOutParam);

  return (
    <div className="results-container">
      {/* Editorial Header */}
      <header className="results-header">
        <div className="results-header-top">
          <div className="results-title-group">
            <span className="results-eyebrow mono">PORTFOLIO INDEX · 2026</span>
            <h1 className="results-title serif-heading">
              {locationParam ? `Residences in ${locationParam}` : 'All Featured Havens'}
            </h1>
          </div>

          {/* Date Selector Banner */}
          <div className="results-date-banner">
            {checkInParam && checkOutParam ? (
              <div className="date-summary-box">
                <CalendarIcon size={16} />
                <span className="mono">
                  {formatEditorialDate(checkInParam)} — {formatEditorialDate(checkOutParam)} ({nights} nights)
                </span>
                <button
                  type="button"
                  onClick={() => setShowDatePicker(!showDatePicker)}
                  className="change-dates-btn"
                >
                  Change
                </button>
              </div>
            ) : (
              <button
                type="button"
                onClick={() => setShowDatePicker(!showDatePicker)}
                className="add-dates-btn"
              >
                <CalendarIcon size={16} />
                <span>Specify Stay Dates for Live Availability</span>
              </button>
            )}

            {showDatePicker && (
              <div className="results-date-popover">
                <DateRangePicker
                  checkIn={tempDates.checkIn}
                  checkOut={tempDates.checkOut}
                  onChange={(d) => setTempDates(d)}
                />
                <div className="popover-actions">
                  <Button variant="ghost" size="sm" onClick={() => setShowDatePicker(false)}>
                    Dismiss
                  </Button>
                  <Button variant="primary" size="sm" onClick={handleApplyDates}>
                    Update Dates
                  </Button>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Filter and Sort Toolbar */}
        <div className="results-toolbar">
          <div className="city-filter-chips" role="group" aria-label="Filter by destination">
            {cities.map((city) => {
              const isActive = (city === 'All' && !locationParam) || locationParam === city;
              return (
                <button
                  key={city}
                  type="button"
                  onClick={() => handleCitySelect(city)}
                  className={`chip ${isActive ? 'active' : ''}`}
                >
                  {city}
                </button>
              );
            })}
          </div>

          <div className="results-sort-group">
            <label htmlFor="sort-select" className="sort-label mono">SORT BY:</label>
            <select
              id="sort-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="sort-select mono"
            >
              <option value="price-asc">Price (Low to High)</option>
              <option value="price-desc">Price (High to Low)</option>
              <option value="name">Alphabetical (A–Z)</option>
            </select>
          </div>
        </div>
      </header>

      {/* Hotel List */}
      <main className="results-main">
        {loading && <HotelListSkeleton />}

        {error && (
          <EmptyState
            title="Service Unavailable"
            description={error}
            action={
              <Button onClick={() => loadHotels(locationParam, checkInParam, checkOutParam)}>
                Retry Search
              </Button>
            }
          />
        )}

        {!loading && !error && sortedHotels.length === 0 && (
          <EmptyState
            title={`No vacancies found${locationParam ? ` in ${locationParam}` : ''}`}
            description="All rooms for this period have been secured, or no properties match your filter. Try clearing dates or selecting an alternative destination."
            action={
              <Button
                variant="secondary"
                onClick={() => {
                  setSelectedCity('All');
                  const newParams = new URLSearchParams();
                  setSearchParams(newParams);
                }}
              >
                View All Destinations
              </Button>
            }
          />
        )}

        {!loading && !error && sortedHotels.length > 0 && (
          <div className="hotel-editorial-list">
            {sortedHotels.map((hotel) => (
              <HotelRow
                key={hotel.id}
                hotel={hotel}
                checkIn={checkInParam}
                checkOut={checkOutParam}
              />
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

