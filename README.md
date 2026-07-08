# 🌍 Booking Tour Application

Booking Tour is a full-stack web application that enables users to browse, book, and manage tour packages across Vietnam. It provides a hierarchical system with distinct roles (ADMIN and USER) for comprehensive tour management, secure booking processing, and user profile tracking.

---

## 🚀 Key Features

* **Secure Authentication:** JWT-based authentication using Spring Security and BCrypt password encryption.
* **Tour Catalog Management:** Complete CRUD operations for tour packages (Admin only).
* **Smart Search & Filter:** Browse public tours with dynamic filtering by destination, duration, and maximum price.
* **Booking Workflow:** Seamless reservation process handling travel dates, guest information, and special requests.
* **Admin Dashboard:** Centralized interface to review, approve, or reject pending bookings and manage overall system data.
* **Dynamic Navigation:** Hierarchical menu system for intuitive site navigation.

---

## 🛠️ Technology Stack

### Backend
* **Framework:** Spring Boot 3.4.6
* **Language:** Java 17
* **Build Tool:** Maven 3.x
* **Security:** Spring Security 6 + JWT (jjwt 0.13.0)
* **ORM & Database:** Spring Data JPA / Hibernate, MySQL 8.x
* **Validation:** Jakarta Bean Validation

### Frontend
* **Template Engine:** Thymeleaf (Server-side rendering)
* **UI/Styling:** HTML5, CSS3, FontAwesome Free 7.1.0
* **Interactivity:** Vanilla JavaScript

---

## 🗄️ Database Schema

The system utilizes a relational database design with the following core entities:

| Entity | Description | Relationships |
| :--- | :--- | :--- |
| **User** | Manages authentication and user profiles. Roles: `ADMIN`, `USER`. | One-to-Many with Bookings |
| **Tour** | Stores tour details (code, price, itinerary, thumbnail, status). | One-to-Many with Bookings |
| **Booking** | Handles reservation records, guest counts, and status (`PENDING`, `APPROVED`, `REJECTED`). | Many-to-One with User & Tour |
| **Menu** | Manages dynamic, hierarchical site navigation. | Self-referencing (Parent/Child) |

---

## 🔐 Security & Authentication Flow

1. **Registration:** Passwords are mathematically hashed using `BCryptPasswordEncoder` before being persisted to the database.
2. **Login:** Upon successful authentication, a JWT is generated (signed via HS256 algorithm).
3. **Session Management:** The application uses a stateless session model. The JWT is attached as an HTTP-only cookie (`AUTH_TOKEN`) with `SameSite=Lax` to prevent XSS and mitigate CSRF vulnerabilities.
4. **Route Protection:** * *Public endpoints:* `/`, `/tours`, `/about`, `/api/auth/**`
   * *Authenticated endpoints:* `/profile`, `/tours/*/book`
   * *Admin endpoints:* `/admin/**`

---

## 🔌 Core API Endpoints

### Authentication (`/api/auth`)
* `POST /register` - Register a new user account.
* `POST /login` - Authenticate and receive an HTTP-only JWT cookie.
* `GET /me` - Retrieve current user profile (Authenticated).
* `POST /logout` - Clear the authentication cookie.

### Public Tours (`/tours`)
* `GET /tours` - Retrieve all active tours (supports query params: `destination`, `maxDays`, `maxPrice`).
* `GET /tours/{code}` - View specific tour details.
* `POST /tours/{code}/book` - Submit a booking request (Authenticated).

### Admin Operations (`/admin`)
* `POST /admin/tours/create` - Create a new tour package.
* `POST /admin/bookings/{id}/approve` - Approve a pending booking.
* `POST /admin/bookings/{id}/reject` - Reject a pending booking.

---

## 💻 Local Setup & Installation

**Prerequisites:**
* Java Development Kit (JDK) 17
* Maven 3.x
* MySQL 8.x

**Steps to run:**

**Clone the repository:**
   ```bash
   git clone [https://github.com/duchung25/Booking-Tour.git](https://github.com/duchung25/Booking-Tour.git)
   cd Booking-Tour
Configure the database:
Create a MySQL database named booking_tour and update your src/main/resources/application.properties:

Properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_tour
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# JWT Secret Configuration
jwt.secret=your_base64_encoded_256_bit_secret_key
Build and Run:

Bash
mvn clean install
mvn spring-boot:run

# Access the application:
Open your browser and navigate to http://localhost:8080.

👤 Author
Nguyễn Đức Hùng
GitHub: @duchung25
Email: hungbnjkl@gmail.com