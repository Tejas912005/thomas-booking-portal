import api from './client';
import { SEED_HOTELS, getStoredBookings, saveStoredBooking } from './mockData';
import { calculateNights } from '../utils/dates';

export async function fetchBookingQuote({ hotelId, checkIn, checkOut }) {
  try {
    const res = await api.post('/bookings/quote', {
      hotelId,
      checkIn,
      checkOut,
    });
    return res.data;
  } catch (err) {
    if (err.error === 'Network Error' || err.status === 500 || !err.status) {
      const hotel = SEED_HOTELS.find((h) => String(h.id) === String(hotelId));
      if (!hotel) throw new Error('Hotel not located');
      const nights = calculateNights(checkIn, checkOut);
      const totalAmount = hotel.pricePerNight * nights;
      return {
        nights,
        pricePerNight: hotel.pricePerNight,
        totalAmount,
      };
    }
    throw err;
  }
}

export async function createBooking({ hotelId, guestName, guestEmail, checkIn, checkOut }) {
  try {
    const res = await api.post('/bookings', {
      hotelId,
      guestName,
      guestEmail,
      checkIn,
      checkOut,
    });
    return res.data;
  } catch (err) {
    if (err.error === 'Network Error' || err.status === 500 || !err.status) {
      const hotel = SEED_HOTELS.find((h) => String(h.id) === String(hotelId));
      const nights = calculateNights(checkIn, checkOut);
      const totalAmount = (hotel?.pricePerNight || 3500) * nights;

      // Generate random HB-XXXXXX reference
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
      let refCode = 'HB-';
      for (let i = 0; i < 6; i++) {
        refCode += chars.charAt(Math.floor(Math.random() * chars.length));
      }

      const newBooking = {
        id: Date.now(),
        reference: refCode,
        hotelId: Number(hotelId),
        hotelName: hotel ? hotel.name : 'The Taj Mahal Palace',
        hotelLocation: hotel ? hotel.location : 'Mumbai',
        pricePerNight: hotel ? hotel.pricePerNight : (nights > 0 ? totalAmount / nights : 3500),
        guestName,
        guestEmail,
        checkIn,
        checkOut,
        nights,
        totalAmount,
        status: 'CONFIRMED',
        createdAt: new Date().toISOString(),
        cancelledAt: null,
      };

      saveStoredBooking(newBooking);
      return newBooking;
    }
    throw err;
  }
}

export async function lookupBooking({ reference, email }) {
  try {
    const res = await api.get('/bookings/lookup', {
      params: { reference, email },
    });
    return res.data;
  } catch (err) {
    if (err.error === 'Network Error' || err.status === 500 || !err.status) {
      const stored = getStoredBookings();
      const match = stored.find(
        (b) =>
          b.reference.toUpperCase() === reference.trim().toUpperCase() &&
          b.guestEmail.toLowerCase() === email.trim().toLowerCase()
      );
      if (match) return match;
      throw {
        status: 404,
        error: 'Booking Not Found',
        message: `No active reservation located for reference "${reference}" and email "${email}".`,
      };
    }
    throw err;
  }
}

export async function cancelBooking(reference, email) {
  try {
    const res = await api.post(`/bookings/${encodeURIComponent(reference)}/cancel`, {
      email,
    });
    return res.data;
  } catch (err) {
    if (err.error === 'Network Error' || err.status === 500 || !err.status) {
      const stored = getStoredBookings();
      const match = stored.find(
        (b) =>
          b.reference.toUpperCase() === reference.trim().toUpperCase() &&
          b.guestEmail.toLowerCase() === email.trim().toLowerCase()
      );
      if (!match) {
        throw {
          status: 404,
          error: 'Booking Not Found',
          message: 'Unable to locate booking to cancel.',
        };
      }
      if (match.status === 'CANCELLED') {
        throw {
          status: 409,
          error: 'Already Cancelled',
          message: 'This reservation has already been cancelled.',
        };
      }
      match.status = 'CANCELLED';
      match.cancelledAt = new Date().toISOString();
      saveStoredBooking(match);
      return match;
    }
    throw err;
  }
}
