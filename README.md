# 🏫 Smart Campus Operations Hub

**IT3030 – Programming Applications and Frameworks | Group Assignment 2026**

A full-stack web system for managing campus facility bookings and maintenance incident ticketing.

---

## 📋 Table of Contents
- [Tech Stack](#tech-stack)
- [Team & Module Ownership](#team--module-ownership)
- [Project Structure](#project-structure)
- [Setup & Run](#setup--run)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [GitHub Actions CI/CD](#github-actions-cicd)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 11, Spring Boot 2.7.x, Spring Data JPA |
| Database | H2 (in-memory, dev) / MySQL (production) |
| Frontend | React 18, Vite, Axios |
| Auth | Spring Security + OAuth 2.0 (Google) |
| CI/CD | GitHub Actions |
| Testing | JUnit 5, Mockito, Spring Boot Test |

---

## 👥 Team & Module Ownership

| Member | Module | Branch | Endpoints |
|---|---|---|---|
| Member 1 | Facilities & Assets Catalogue | `member-1` | `GET/POST/PUT/DELETE /api/resources` |
| Member 2 (Team Lead) | Booking Management + Integration | `member-2` | `GET/POST/PUT/DELETE /api/bookings` |
| Member 3 | Incident Ticketing + Attachments + Comments | `member-3` | `GET/POST/PUT/DELETE /api/tickets` |
| Member 4 | Notifications + OAuth 2.0 Auth | `member-4` | `GET/POST /api/notifications`, `/oauth2/**` |

---

## 📁 Project Structure

```
smart-campus/
├── .github/workflows/        # GitHub Actions CI/CD
│   └── main.yml
├── backend/                  # Spring Boot REST API
│   └── src/main/java/com/smartcampus/
│       ├── booking/          # Module B – Booking Management (Member 2)
│       │   ├── controller/BookingController.java
│       │   ├── service/BookingService.java
│       │   ├── repository/BookingRepository.java
│       │   ├── model/Booking.java
│       │   └── dto/BookingRequest.java
│       └── ticket/           # Module C – Incident Ticketing (Member 3)
│           ├── controller/TicketController.java
│           ├── service/TicketService.java
│           ├── repository/TicketRepository.java
│           └── model/
├── frontend/                 # React + Vite
│   └── src/
│       ├── api/              # Axios service layer
│       ├── components/
│       │   ├── bookings/     # BookingForm, MyBookings, AdminBookings
│       │   └── tickets/      # TicketForm, MyTickets, AdminTickets, TicketDetail
│       └── App.jsx
├── postman/                  # Postman API collection
│   └── SmartCampus.postman_collection.json
├── evidence/                 # Screenshots & testing evidence
├── docker-compose.yml        # Run full stack with Docker
└── README.md
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 11+
- Node.js 18+
- Maven 3.8+ (or use `./mvnw`)

### 1. Run Backend

```powershell
# Windows PowerShell
.\run-backend.ps1

# Or manually
cd backend
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080**
H2 Console: **http://localhost:8080/h2-console**

### 2. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at: **http://localhost:5173**

### 3. Run with Docker (Optional)

```bash
docker-compose up --build
```

---

## 📡 API Endpoints

### Module B – Booking Management (Member 2)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/bookings` | Create a booking | USER |
| `GET` | `/api/bookings/my?userId={id}` | Get my bookings | USER |
| `GET` | `/api/bookings/all` | Get all bookings | ADMIN |
| `GET` | `/api/bookings/{id}` | Get booking by ID | USER |
| `PUT` | `/api/bookings/{id}/status` | Update booking status | ADMIN/USER |
| `DELETE` | `/api/bookings/{id}` | Delete a booking | ADMIN |

### Module C – Incident Ticketing (Member 3)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/tickets` | Create incident ticket | USER |
| `GET` | `/api/tickets/my?userId={id}` | Get my tickets | USER |
| `GET` | `/api/tickets/all` | Get all tickets | ADMIN |
| `GET` | `/api/tickets/{id}` | Get ticket by ID | USER |
| `PUT` | `/api/tickets/{id}/status` | Update status | ADMIN |
| `PUT` | `/api/tickets/{id}/assign` | Assign technician | ADMIN |
| `POST` | `/api/tickets/{id}/attachments` | Upload image (max 3) | USER |
| `POST` | `/api/tickets/{id}/comments` | Add comment | USER |
| `GET` | `/api/tickets/{id}/comments` | Get comments | USER |
| `PUT` | `/api/tickets/{id}/comments/{cId}` | Edit comment (owner only) | USER |
| `DELETE` | `/api/tickets/{id}/comments/{cId}` | Delete comment (owner only) | USER |
| `DELETE` | `/api/tickets/{id}` | Delete ticket | ADMIN |

---

## 🧪 Testing

### Run Unit Tests

```bash
cd backend
mvn test
```

### Postman Collection

Import `postman/SmartCampus.postman_collection.json` into Postman to test all endpoints.

---

## 🔄 GitHub Actions CI/CD

A GitHub Actions workflow runs automatically on every push to verify the build and run all tests.

See `.github/workflows/main.yml` for details.

---

## 🔐 Security Notes

- All endpoints are protected by Spring Security
- Role-based access: `USER` and `ADMIN`
- OAuth 2.0 (Google) for authentication
- File upload restricted to images (JPEG, PNG, GIF, WEBP), max 5MB each

---

## 📝 Submission

- **Deadline:** 27th April 2026, 11:45 PM (GMT+5:30)
- **Report:** IT3030_PAF_Assignment_2026_GroupXX.pdf
