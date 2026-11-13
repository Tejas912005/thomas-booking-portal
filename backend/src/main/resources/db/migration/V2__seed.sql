-- ============================================================
-- V2 – ThomasCook Hotel Booking Portal Seed Data (Flyway migration)
-- Uses INSERT IGNORE / ON DUPLICATE for idempotent re-runs
-- ============================================================

-- ------------------------------------------------------------
-- Hotels
-- ------------------------------------------------------------
INSERT IGNORE INTO hotels (id, name, location, description, amenities, price_per_night, total_rooms) VALUES
(1, 'The Taj Mahal Palace',             'Mumbai',    'The legendary sea-facing palace hotel in Colaba overlooking the Gateway of India. Renowned since 1903 for regal luxury.',
 'Sea View Suites, Swimming Pool, Jiva Luxury Spa, 9 Iconic Restaurants, Free Wi-Fi, Butler Service', 12500.00,  2),
(2, 'Taj Exotica Resort & Spa',          'Goa',       'A Mediterranean-inspired beachfront paradise sprawled across 56 acres along Benaulim Beach in South Goa.',
 'Private Beach Access, Infinity Pool, Jiva Spa, 9-Hole Golf Course, Beachside Bar, Free Wi-Fi',       14500.00, 15),
(3, 'Wildflower Hall, An Oberoi Resort', 'Shimla',    'Perched 8,250 feet above sea level in the Himalayas amidst cedar woods, featuring heated open-air whirlpools.',
 'Outdoor Heated Whirlpool, Cedar Forest Treks, Fireplace Suites, Oberoi Luxury Spa, Free Wi-Fi',     16500.00,  8),
(4, 'The Leela Palace',                  'Bangalore', 'An architectural wonder inspired by the Royal Palace of Mysore, set in nine acres of lush cascading gardens.',
 'Royal Club Lounge, Lagoon Pool, Award-Winning Spa, Fine Dining, High-Speed Wi-Fi',                   11000.00, 12),
(5, 'Suryagarh',                         'Jaisalmer', 'A breathtaking fortress hotel celebrating the golden heritage of the Thar Desert with grand sun-lit courtyards.',
 'Thar Desert Camel Safaris, Courtyard Dining, Indoor Heated Pool, Rait Spa, Free Wi-Fi',              13000.00,  6),
(6, 'The Tamara Coorg',                  'Coorg',     'An enchanting eco-luxury retreat nestled across 180 acres of organic coffee and cardamom plantations in the Western Ghats.',
 'Coffee Plantation Treks, Private Jacuzzi Villas, Elevation Spa, Organic Dining, Free Wi-Fi',          13500.00, 10);

-- ------------------------------------------------------------
-- Room inventory – 120 calendar days per hotel via MySQL 8 CTE
-- ------------------------------------------------------------
INSERT IGNORE INTO room_inventory (hotel_id, stay_date, total_rooms, booked_rooms)
WITH RECURSIVE day_sequence AS (
    SELECT 0 AS day_offset
    UNION ALL
    SELECT day_offset + 1 FROM day_sequence WHERE day_offset < 119
)
SELECT 
    h.id AS hotel_id,
    DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY) AS stay_date,
    h.total_rooms AS total_rooms,
    0 AS booked_rooms
FROM hotels h
CROSS JOIN day_sequence d;
