import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import NavBar from './components/NavBar';
import HomePage from './pages/HomePage';
import ResultsPage from './pages/ResultsPage';
import HotelDetailPage from './pages/HotelDetailPage';
import BookingPage from './pages/BookingPage';
import ConfirmationPage from './pages/ConfirmationPage';
import ManageBookingPage from './pages/ManageBookingPage';
import EmptyState from './components/EmptyState';
import Button from './components/Button';
import './styles/global.css';

export default function App() {
  const [theme, setTheme] = useState(() => {
    return localStorage.getItem('grand_tour_theme') || 'light';
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('grand_tour_theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  return (
    <BrowserRouter>
      <div className="app-shell">
        <NavBar theme={theme} toggleTheme={toggleTheme} />
        <div className="app-content">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/hotels" element={<ResultsPage />} />
            <Route path="/hotels/:id" element={<HotelDetailPage />} />
            <Route path="/book" element={<BookingPage />} />
            <Route path="/confirmation" element={<ConfirmationPage />} />
            <Route path="/manage" element={<ManageBookingPage />} />
            <Route
              path="*"
              element={
                <div style={{ maxWidth: '600px', margin: '4rem auto', padding: '0 1.5rem' }}>
                  <EmptyState
                    title="Folio Not Found"
                    description="The dispatch or destination you are searching for does not exist in our catalog."
                    action={
                      <Link to="/">
                        <Button variant="primary">Return to Journal Home</Button>
                      </Link>
                    }
                  />
                </div>
              }
            />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}
