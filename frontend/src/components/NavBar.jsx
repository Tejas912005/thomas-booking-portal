import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './NavBar.css';
import { SunIcon, MoonIcon, MapPinIcon } from './Icons';

export default function NavBar({ theme, toggleTheme }) {
  const location = useLocation();

  return (
    <header className="site-header no-print">
      <div className="header-glass-overlay" />
      <div className="site-header-inner">
        <Link to="/" className="site-logo">
          <div className="logo-brand-mark">
            <span className="logo-icon-dot" />
            <span className="logo-title">Grand Tour Hotels</span>
          </div>
          <span className="logo-tagline">Boutique Stays Across India</span>
        </Link>

        <nav className="site-nav" aria-label="Main Navigation">
          <Link
            to="/hotels"
            className={`nav-link ${location.pathname === '/hotels' ? 'active' : ''}`}
          >
            Explore Hotels
          </Link>
          <Link
            to="/manage"
            className={`nav-link ${location.pathname === '/manage' ? 'active' : ''}`}
          >
            My Booking
          </Link>
          
          <button
            type="button"
            className="theme-toggle"
            onClick={toggleTheme}
            aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
            title={`Toggle ${theme === 'dark' ? 'light' : 'dark'} mode`}
          >
            {theme === 'dark' ? <SunIcon size={16} /> : <MoonIcon size={16} />}
          </button>
        </nav>
      </div>
    </header>
  );
}
