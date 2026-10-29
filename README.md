# Room Booking System

![CI](https://github.com/kattyalice/room-booking-system/actions/workflows/ci.yml/badge.svg)

A full-stack room and resource booking application that prevents double-bookings. Users can manage rooms and reserve time slots, and the system guarantees no two confirmed bookings for the same room ever overlap.

> **Status: In progress.** The backend API is complete and tested. The Angular frontend and deployment are underway. See the [roadmap](#roadmap).

## Highlights

- **Double-booking prevention at the database level.** A PostgreSQL exclusion constraint (`EXCLUDE USING gist` with time ranges) rejects overlapping bookings, so the rule holds even when two requests arrive at the same moment. Back-to-back bookings (9:00–10:00 and 10:00–11:00) are allowed using half-open `[)` ranges, and cancelled bookings free their slot.
- **Layered architecture:** Controller → Service → Repository, with DTOs (Java records) separating the API contract from the database entities.
- **Consistent error handling:** a global exception handler returns standard [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem responses with the correct status codes (400, 404, 409) and field-level validation messages.
- **Tested at two levels:** service tests with JUnit and Mockito cover the business rules, and MockMvc tests cover status codes, validation and JSON responses. GitHub Actions runs the full suite on every push against a real PostgreSQL database.
- **Versioned schema migrations** with Flyway, and Hibernate validates every entity against the real schema on startup.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring Data JPA, Bean Validation |
| Database | PostgreSQL 17, Flyway migrations |
| Testing | JUnit 5, Mockito, MockMvc |
| API docs | springdoc-openapi (Swagger UI) |
| CI | GitHub Actions |
| Frontend | Angular *(in progress)* |
| Tooling | Maven, Docker, Git |

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/rooms` | List all rooms |
| GET | `/api/rooms/{id}` | Get one room |
| POST | `/api/rooms` | Create a room |
| PUT | `/api/rooms/{id}` | Update a room |
| POST | `/api/bookings` | Create a booking (409 if it overlaps) |
| GET | `/api/bookings/{id}` | Get one booking |
| GET | `/api/rooms/{roomId}/bookings` | List a room's bookings, earliest first |
| POST | `/api/bookings/{id}/cancel` | Cancel a booking |

**Example error response (409):**

```json
{
  "status": 409,
  "title": "Conflict",
  "detail": "Room B is already booked for that time.",
  "instance": "/api/bookings"
}
```

## Testing

Run all tests from the `backend` folder:

```bash
./mvnw test
```

On Windows: `mvnw.cmd test`

- **Service tests** (JUnit + Mockito): business rules such as overlap handling and cancellation
- **Controller tests** (MockMvc): status codes, validation errors and JSON responses
- **Application test:** confirms the full app starts against a real database

Every push runs the full suite on GitHub Actions.

## Running Locally

**Prerequisites:** Java 21, Docker

1. Start PostgreSQL:
   ```bash
   docker run --name booking-db -e POSTGRES_USER=booking -e POSTGRES_PASSWORD=booking -e POSTGRES_DB=booking -p 5433:5432 -d postgres:17
   ```
2. Run the API (Flyway creates the tables automatically on startup):
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   On Windows: `mvnw.cmd spring-boot:run`
3. The API is available at `http://localhost:8080/api/rooms`
4. Interactive API docs (Swagger UI): `http://localhost:8080/swagger-ui/index.html`

## Roadmap

- [x] Database schema with overlap-prevention constraint
- [x] Room and Booking entities and repositories
- [x] Rooms API (create, read, update) with validation
- [x] Global error handling with problem responses
- [x] Bookings API (create, cancel, list by room)
- [x] Unit and controller tests (JUnit, Mockito, MockMvc)
- [x] CI with GitHub Actions
- [x] Interactive API docs (Swagger UI)
- [ ] Angular frontend
- [ ] Deployment

## About

Built by **Kathryn Baldridge**. I built this to learn Java, Spring Boot, and Angular, and chose a booking system because preventing double-bookings is a real-world concurrency problem.

[GitHub](https://github.com/kattyalice) · [LinkedIn](https://www.linkedin.com/in/kathryn-baldridge/)