-- ============================================================
-- ThomasCook Hotel Booking Portal – Plain MySQL 8 Schema
-- Drop in reverse FK order before recreating
-- ============================================================

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS room_inventory;
DROP TABLE IF EXISTS hotels;

-- ------------------------------------------------------------
-- hotels
-- ------------------------------------------------------------
CREATE TABLE hotels (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(120)    NOT NULL,
    location        VARCHAR(120)    NOT NULL,
    description     TEXT            NOT NULL,
    amenities       TEXT            NOT NULL,
    price_per_night DECIMAL(10,2)   NOT NULL,
    total_rooms     INT             NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_hotels_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- room_inventory
-- ------------------------------------------------------------
CREATE TABLE room_inventory (
    id           BIGINT  NOT NULL AUTO_INCREMENT,
    hotel_id     BIGINT  NOT NULL,
    stay_date    DATE    NOT NULL,
    total_rooms  INT     NOT NULL,
    booked_rooms INT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_hotel_date (hotel_id, stay_date),
    INDEX idx_inventory_hotel_date (hotel_id, stay_date),
    CONSTRAINT fk_inventory_hotel FOREIGN KEY (hotel_id) REFERENCES hotels (id),
    CONSTRAINT chk_booked_rooms CHECK (booked_rooms BETWEEN 0 AND total_rooms)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- bookings
-- ------------------------------------------------------------
CREATE TABLE bookings (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    reference    VARCHAR(20)     NOT NULL,
    hotel_id     BIGINT          NOT NULL,
    guest_name   VARCHAR(200)    NOT NULL,
    guest_email  VARCHAR(255)    NOT NULL,
    check_in     DATE            NOT NULL,
    check_out    DATE            NOT NULL,
    nights       INT             NOT NULL,
    total_amount DECIMAL(12,2)   NOT NULL,
    status       ENUM('CONFIRMED','CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP       NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_booking_reference (reference),
    INDEX idx_bookings_reference  (reference),
    INDEX idx_bookings_guest_email (guest_email),
    INDEX idx_bookings_hotel_id    (hotel_id),
    CONSTRAINT fk_bookings_hotel FOREIGN KEY (hotel_id) REFERENCES hotels (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
