# ThomasCook Hotel Booking Portal

> A full-stack Hotel Booking Portal built with Spring Boot 3, Java 17, MySQL 8, and React 19 (Vite). Features real-time per-night inventory tracking, pessimistic concurrency locking for the contested last room, smart alternative date recommendations, and multi-channel booking dispatch (real Gmail SMTP, 1-tap WhatsApp, and .ics calendar sync).

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17+, Spring Boot 3.2, Spring Data JPA, Bean Validation, Flyway Migrations, Maven |
| **Frontend** | React 19, Vite 8, React Router v7, Axios, Plain CSS Design System (CSS tokens, zero UI kits) |
| **Database** | MySQL 8 (InnoDB, utf8mb4) |
| **Testing** | JUnit 5, Mockito, Spring Boot Test, LastRoomConcurrencyIT |

---

## Prerequisites

- **Java**: 17 or higher
- **Maven**: 3.8+ (or Maven Wrapper)
- **Node.js**: 18+ & npm
- **MySQL**: 8.0+

---

## MySQL Setup

Run the following in your MySQL CLI or workbench:

```sql
CREATE DATABASE hotel_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Database Initialization:
- **Automatic (Recommended)**: Spring Boot executes Flyway migrations (`V1__schema.sql` and `V2__seed.sql`) automatically on application startup.
- **Manual (Mirror SQL)**:
  ```bash
  mysql -u root -p hotel_booking < db/schema.sql
  mysql -u root -p hotel_booking < db/seed.sql
  ```

---

## Running the Backend

Configure your MySQL connection via environment variables (or copy `.env.example` to `.env`):

### PowerShell (Windows)
```powershell
cd backend
$env:DB_URL="jdbc:mysql://localhost:3306/hotel_booking?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER="root"
$env:DB_PASSWORD="your_password"

# Optional: Real Gmail dispatch
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-16-char-app-password"

mvn spring-boot:run
```

### Bash (macOS / Linux)
```bash
cd backend
export DB_URL="jdbc:mysql://localhost:3306/hotel_booking?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USER=root
export DB_PASSWORD=your_password

# Optional: Real Gmail dispatch
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-16-char-app-password"

mvn spring-boot:run
```

The backend server listens on `http://localhost:8080`.

---

## Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server launches at `http://localhost:5173`. CORS is pre-configured on the backend to allow requests from `http://localhost:5173`.

To verify a production build:
```bash
cd frontend
npm run build
```

---

## Running the Tests

```bash
cd backend

# Run unit tests:
mvn test

# Run all tests including the concurrency integration test:
mvn test -Dtest=*Test,*IT
```

> **Note on Concurrency Test:** `LastRoomConcurrencyIT` tests true parallel database row locking using `SELECT ... FOR UPDATE` against MySQL InnoDB. Ensure MySQL is accessible with the test environment credentials before running integration tests.

---

## REST API Overview

All API endpoints follow RESTful conventions and return standardized JSON responses.

| Method | Path | Request Body / Query Params | Description |
|---|---|---|---|
| `GET` | `/api/hotels` | `?location=&checkIn=&checkOut=` | List residences with real-time room availability counts for dates |
| `GET` | `/api/hotels/{id}` | `?checkIn=&checkOut=` | Retrieve property details and nightly calendar availability |
| `POST` | `/api/bookings/quote` | `{ hotelId, checkIn, checkOut }` | Stateless quote calculating nights and total amount (`BigDecimal`) |
| `POST` | `/api/bookings` | `{ hotelId, guestName, guestEmail, checkIn, checkOut }` | Confirm reservation with pessimistic write lock on inventory |
| `GET` | `/api/bookings/lookup` | `?reference=&email=` | Retrieve confirmed or cancelled folio by reference & email |
| `POST` | `/api/bookings/{reference}/cancel` | `{ email }` | Cancel booking and immediately restore nightly room inventory |

### Standardized Error Shape (GlobalExceptionHandler)

All exceptions return a unified JSON payload without leaking internal stack traces:

```json
{
  "timestamp": "2026-11-12T19:45:00",
  "status": 409,
  "error": "No Availability",
  "message": "No rooms available for the selected dates",
  "path": "/api/bookings",
  "fieldErrors": [],
  "nearestDates": [
    {
      "checkIn": "2026-11-20",
      "checkOut": "2026-11-23",
      "nights": 3,
      "totalAmount": 10500.00
    }
  ]
}
```

---

## Key Assumptions & Booking Rules

1. **Closed-Open Stay Interval `[checkIn, checkOut)`**: Check-out day is not counted as an occupied night. A guest departing on the 15th frees the room for another guest checking in on the 15th.
2. **Per-Night Inventory Model**: Nightly availability is tracked independently in `room_inventory`. Back-to-back and overlapping stays are handled natively.
3. **Pessimistic Concurrency**: Contested rooms are locked via `SELECT ... FOR UPDATE` ordered by `stay_date ASC`, preventing deadlocks and race conditions.
4. **CHECK Constraint Last Line of Defence**: Database-level constraint `CHECK (booked_rooms >= 0 AND booked_rooms <= total_rooms)` prevents overbooking even if application checks were somehow bypassed.
5. **Idempotent / Conflict Cancellations**: Cancelling an already cancelled reservation returns HTTP 409 Conflict with a clear message.
6. **Currency & Precision**: All prices are in Indian Rupees (INR) calculated using arbitrary-precision `BigDecimal`.

---

## Known Limitations

- Multi-room bookings per transaction are not supported (scope limited to 1 room per booking).
- Designed without external payment gateways (reservations confirm directly upon booking).
