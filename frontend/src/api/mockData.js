// Real famous, top luxury hotels in India
export const SEED_HOTELS = [
  {
    id: 1,
    name: 'The Taj Mahal Palace',
    location: 'Mumbai',
    description: 'The legendary sea-facing palace hotel in Colaba overlooking the Gateway of India. Renowned since 1903 for regal luxury, historic architecture, and world-class service.',
    amenities: 'Sea View Suites, Swimming Pool, Jiva Luxury Spa, 9 Iconic Restaurants, Free Wi-Fi, Butler Service',
    pricePerNight: 12500.00,
    totalRooms: 2, // Last room demo hotel
    availableRooms: 2,
    rating: 4.9,
    reviewsCount: 1420,
    imageUrl: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80',
    tag: 'Iconic Palace',
  },
  {
    id: 2,
    name: 'Taj Exotica Resort & Spa',
    location: 'Goa',
    description: 'A Mediterranean-inspired beachfront luxury paradise sprawled across 56 acres along Benaulim Beach in South Goa, featuring tropical gardens and an endless sea horizon.',
    amenities: 'Private Beach Access, Infinity Pool, Jiva Spa, 9-Hole Golf Course, Beachside Bar, Free Wi-Fi',
    pricePerNight: 14500.00,
    totalRooms: 15,
    availableRooms: 15,
    rating: 4.9,
    reviewsCount: 980,
    imageUrl: 'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1200&q=80',
    tag: 'Beach Resort',
  },
  {
    id: 3,
    name: 'Wildflower Hall, An Oberoi Resort',
    location: 'Shimla',
    description: 'Perched 8,250 feet above sea level in the Shimla Himalayas amidst dense pine and cedar woods. Features heated open-air whirlpools and panoramic snow-capped mountain views.',
    amenities: 'Outdoor Heated Whirlpool, Cedar Forest Treks, Fireplace Suites, Oberoi Luxury Spa, Free Wi-Fi',
    pricePerNight: 16500.00,
    totalRooms: 8,
    availableRooms: 8,
    rating: 4.9,
    reviewsCount: 650,
    imageUrl: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80',
    tag: 'Himalayan Luxury',
  },
  {
    id: 4,
    name: 'The Leela Palace',
    location: 'Bangalore',
    description: 'An architectural wonder inspired by the Royal Palace of Mysore, nestled inside nine acres of cascading waterfalls, sparkling lagoons, and manicured royal gardens.',
    amenities: 'Royal Club Lounge, Lagoon Pool, Award-Winning Spa, Fine Dining, High-Speed Wi-Fi, Fitness Studio',
    pricePerNight: 11000.00,
    totalRooms: 12,
    availableRooms: 12,
    rating: 4.8,
    reviewsCount: 1120,
    imageUrl: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1200&q=80',
    tag: 'Royal Palace',
  },
  {
    id: 5,
    name: 'Suryagarh',
    location: 'Jaisalmer',
    description: 'A breathtaking sun-drenched fortress hotel celebrating the golden heritage of the Thar Desert, with majestic sun-lit courtyards, traditional folk music, and dune safaris.',
    amenities: 'Thar Desert Camel Safaris, Courtyard Dining, Indoor Heated Pool, Rait Spa, Free Wi-Fi',
    pricePerNight: 13000.00,
    totalRooms: 6,
    availableRooms: 6,
    rating: 4.9,
    reviewsCount: 840,
    imageUrl: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80',
    tag: 'Desert Fortress',
  },
  {
    id: 6,
    name: 'The Tamara Coorg',
    location: 'Coorg',
    description: 'An enchanting eco-luxury retreat nestled across 180 acres of organic coffee and cardamom plantations on the slopes of the Western Ghats with babbling mountain streams.',
    amenities: 'Coffee Plantation Treks, Private Jacuzzi Villas, Elevation Spa, Organic Dining, Free Wi-Fi',
    pricePerNight: 13500.00,
    totalRooms: 10,
    availableRooms: 10,
    rating: 4.8,
    reviewsCount: 520,
    imageUrl: 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1200&q=80',
    tag: 'Rainforest Estate',
  },
];

// In-browser mock registry for seamless demo when backend service is offline
const STORAGE_KEY = 'grand_tour_mock_bookings';

export function getStoredBookings() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
  } catch {
    return [];
  }
}

export function saveStoredBookings(bookings) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(bookings));
  } catch (e) {
    console.error('Failed to save to localStorage', e);
  }
}

export function saveStoredBooking(booking) {
  try {
    const list = getStoredBookings();
    const idx = list.findIndex((b) => b.reference === booking.reference);
    if (idx >= 0) {
      list[idx] = booking;
    } else {
      list.unshift(booking);
    }
    saveStoredBookings(list);
  } catch (e) {
    console.error('Failed to update booking in localStorage', e);
  }
}
