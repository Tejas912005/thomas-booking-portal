import api from './client';
import { SEED_HOTELS } from './mockData';

export async function fetchHotels({ location = '', checkIn = '', checkOut = '' } = {}) {
  try {
    const params = {};
    if (location) params.location = location;
    if (checkIn) params.checkIn = checkIn;
    if (checkOut) params.checkOut = checkOut;
    const res = await api.get('/hotels', { params });
    return res.data;
  } catch (err) {
    // If backend connection fails, provide graceful offline fallback for demo
    if (err.error === 'Network Error' || err.status === 500 || !err.status) {
      console.info('[Portal] Backend offline; serving mirrored seed catalog.');
      let results = [...SEED_HOTELS];
      if (location && location !== 'All') {
        results = results.filter((h) =>
          h.location.toLowerCase().includes(location.toLowerCase())
        );
      }
      return results;
    }
    throw err;
  }
}

export async function fetchHotelById(id, { checkIn = '', checkOut = '' } = {}) {
  try {
    const params = {};
    if (checkIn) params.checkIn = checkIn;
    if (checkOut) params.checkOut = checkOut;
    const res = await api.get(`/hotels/${id}`, { params });
    return res.data;
  } catch (err) {
    if (err.error === 'Network Error' || err.status === 500 || !err.status) {
      const found = SEED_HOTELS.find((h) => String(h.id) === String(id));
      if (found) {
        return {
          ...found,
          roomAvailability: [
            { date: checkIn || '2026-11-20', available: found.availableRooms },
            { date: checkOut || '2026-11-22', available: found.availableRooms },
          ],
        };
      }
    }
    throw err;
  }
}
