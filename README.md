# Room Booking System

![CI](https://github.com/kattyalice/room-booking-system/actions/workflows/ci.yml/badge.svg)

A full-stack room and resource booking application that prevents double-bookings. Users can manage rooms and reserve time slots, and the system guarantees no two confirmed bookings for the same room ever overlap.

**[Live demo](https://room-booking-system-ruby.vercel.app)** · **[API docs (Swagger)](https://room-booking-system-zc46.onrender.com/swagger-ui/index.html)**

> The backend runs on Render's free tier and goes to sleep when idle, so the first load can take about 50 seconds. After that it's fast.

![Rooms page](docs/screenshots/rooms.png)

![Room detail page with bookings and a double-booking error](docs/screenshots/room-detail.png)

## Highlights

- **Double-booking prevention at the database level.** A PostgreSQL exclusion constraint (`EXCLUDE USING gist` with time ranges) rejects overlapping bookings, so the rule holds even when two requests arrive at the same moment. Back-to-back bookings (9:00–10:00 and 10:00–11:00) are allowed using half-open `[)` ranges, and cancelled bookings free their slot.
- **Layered architecture:** Controller → Service → Repository, with DTOs (Java records) separating the API contract from the database entities.
- **Consistent error handling:** a global exception handler returns standard [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem responses with the correct status codes (400, 404, 409) and field-level validation messages.
- **Errors shown where users need them:** the Angular frontend displays the API's validation messages under each form field, and conflicts such as double-bookings or duplicate room names in a message at the top of the form.
- **Time zones handled correctly:** times are stored in UTC and shown in the user's local time zone.
- **Responsive design:** built with Tailwind CSS and Heroicons, the layout adapts from phones to wide monitors.
- **Tested at two levels:** service tests with JUnit and Mockito cover the business rules, and MockMvc tests cover status codes, validation and JSON responses. GitHub Actions runs the full suite on every push against a real PostgreSQL database.
- **Versioned schema migrations** with Flyway, and Hibernate validates every entity against the real schema on startup.
- **Deployed across three services,** with configuration supplied through environment variables, so the same code runs locally and in production.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring Data JPA, Bean Validation |
| Database | PostgreSQL 17, Flyway migrations |
| Frontend | Angular 21 (standalone components, signals, reactive forms, routing), TypeScript, Tailwind CSS, Heroicons |
| Testing | JUnit 5, Mockito, MockMvc |
| API docs | springdoc-openapi (Swagger UI) |
| CI | GitHub Actions |
| Hosting | Vercel (frontend), Render with Docker (backend), Neon (PostgreSQL) |
| Tooling | Maven, npm, Docker, Git |

## Architecture

```
Browser ──► Vercel (Angular) ──► Render (Spring Boot in Docker) ──► Neon (PostgreSQL)
```

- **Vercel** builds and serves the Angular app. A rewrite rule (`vercel.json`) sends every path to `index.html`, so Angular's router handles routes like `/rooms/2`, even on refresh.
- **Render** builds the backend from a multi-stage `Dockerfile` and runs it. The port, database connection and allowed CORS origin all come from environment variables.
- **Neon** hosts PostgreSQL. Flyway creates and versions the schema, including the overlap-prevention constraint.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/rooms` | List all rooms, sorted by name |
| GET | `/api/rooms/{id}` | Get one room |
| POST | `/api/rooms` | Create a room (409 if the name already exists) |
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
  "detail": "Conference Room is already booked for that time.",
  "instance": "/api/bookings"
}
```

## Frontend

The Angular app in `frontend/` talks to the API:

- **Rooms page:** browse all rooms in alphabetical order and add new ones, with the API's validation messages shown under each field. New rooms appear in the list right away, in the right place.
- **Room detail page:** open a room to see its bookings, sorted by start time and shown in your local time zone. Cancelled bookings are greyed out.
- **Booking:** book a room from its detail page. A double-booking shows the API's 409 message.
- **Cancelling:** cancel a confirmed booking, which frees its time slot for someone else.
- **Error states:** clear messages when a room doesn't exist or the server can't be reached.
- **Responsive layout:** forms and lists sit side by side on wide screens and stack on phones.

The API address lives in `src/environments/`: the development file points at `localhost`, and the production file points at the deployed backend.

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

**Prerequisites:** Java 21, Docker, Node.js and the Angular CLI (`npm install -g @angular/cli`)

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
5. In a second terminal, run the frontend:
   ```bash
   cd frontend
   npm install
   ng serve
   ```
6. Open the app at `http://localhost:4200`

### Configuration

The backend reads these environment variables, with defaults for local development:

| Variable | Purpose | Local default |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5433/booking` |
| `DB_USERNAME` | database user | `booking` |
| `DB_PASSWORD` | database password | `booking` |
| `PORT` | HTTP port | `8080` |
| `CORS_ALLOWED_ORIGINS` | frontend address allowed to call the API | `http://localhost:4200` |

## Roadmap

- [x] Database schema with overlap-prevention constraint
- [x] Room and Booking entities and repositories
- [x] Rooms API (create, read, update) with validation
- [x] Global error handling with problem responses
- [x] Bookings API (create, cancel, list by room)
- [x] Unit and controller tests (JUnit, Mockito, MockMvc)
- [x] CI with GitHub Actions
- [x] Interactive API docs (Swagger UI)
- [x] Angular frontend: rooms list and form, room detail with routing
- [x] Booking form with validation and conflict messages
- [x] Cancel bookings, error states, environment-based API config
- [x] Responsive styling with Tailwind CSS and Heroicons
- [x] Deployment (Vercel, Render with Docker, Neon)

**Possible next steps:** authentication, editing rooms from the UI, a weekly calendar view, and frontend tests.

## About

Built by **Kathryn Baldridge**. I built this to learn Java, Spring Boot, and Angular, and chose a booking system because preventing double-bookings is a real-world concurrency problem.

[GitHub](https://github.com/kattyalice) · [LinkedIn](https://www.linkedin.com/in/kathryn-baldridge/)