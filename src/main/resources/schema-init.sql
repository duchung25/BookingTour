-- =====================================================
-- SCHEMA INIT - Tạo bảng (idempotent - chạy được nhiều lần)
-- Spring Boot tự chạy file này khi khởi động.
-- =====================================================

CREATE DATABASE IF NOT EXISTS tourbookingdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tourbookingdb;

-- =========================
-- 1. ROLES
-- =========================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- =========================
-- 2. USERS
-- =========================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) UNIQUE,
    gender VARCHAR(10),
    date_of_birth DATE,
    address VARCHAR(255),
    avatar_url VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_password_reset_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =========================
-- 3. USER AUTH PROVIDERS
-- =========================
CREATE TABLE IF NOT EXISTS user_auth_providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    provider_user_id VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    provider_email VARCHAR(100) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_auth_providers_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_user_provider UNIQUE (user_id, provider),
    CONSTRAINT uq_provider_user UNIQUE (provider, provider_user_id)
);

-- =========================
-- 4. TOUR CATEGORIES
-- =========================
CREATE TABLE IF NOT EXISTS tour_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

-- =========================
-- 5. DESTINATIONS
-- =========================
CREATE TABLE IF NOT EXISTS destinations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    province VARCHAR(100),
    country VARCHAR(100) NOT NULL DEFAULT 'Việt Nam',
    description TEXT,
    image_url VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

-- =========================
-- 6. TOURS
-- =========================
CREATE TABLE IF NOT EXISTS tours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    destination_id BIGINT NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    departure_location VARCHAR(150) NOT NULL,
    duration_days INT NOT NULL,
    duration_nights INT NOT NULL,
    transport VARCHAR(100),
    hotel_standard VARCHAR(50),
    description TEXT,
    policy TEXT,
    included_services TEXT,
    excluded_services TEXT,
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tours_category FOREIGN KEY (category_id) REFERENCES tour_categories (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_tours_destination FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =========================
-- 7. TOUR IMAGES
-- =========================
CREATE TABLE IF NOT EXISTS tour_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    alt_text VARCHAR(255),
    is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tour_images_tour FOREIGN KEY (tour_id) REFERENCES tours (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =========================
-- 8. TOUR ITINERARIES
-- =========================
CREATE TABLE IF NOT EXISTS tour_itineraries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    meals VARCHAR(100),
    accommodation VARCHAR(150),
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_tour_itineraries_tour FOREIGN KEY (tour_id) REFERENCES tours (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =========================
-- 9. TOUR DEPARTURES
-- =========================
CREATE TABLE IF NOT EXISTS tour_departures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    departure_date DATE NOT NULL,
    return_date DATE NOT NULL,
    capacity INT NOT NULL,
    available_slots INT NOT NULL,
    adult_price DECIMAL(15, 2) NOT NULL,
    child_price DECIMAL(15, 2) DEFAULT 0,
    infant_price DECIMAL(15, 2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tour_departures_tour FOREIGN KEY (tour_id) REFERENCES tours (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_tour_departures_capacity CHECK (capacity >= 0),
    CONSTRAINT chk_tour_departures_available_slots CHECK (available_slots >= 0),
    CONSTRAINT chk_tour_departures_date CHECK (return_date >= departure_date)
);

-- =========================
-- 10. PROMOTIONS
-- =========================
CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(15, 2) NOT NULL,
    max_discount_amount DECIMAL(15, 2),
    min_booking_amount DECIMAL(15, 2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    usage_limit INT,
    used_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_promotions_discount_value CHECK (discount_value >= 0),
    CONSTRAINT chk_promotions_used_count CHECK (used_count >= 0)
);

-- =========================
-- 11. TOUR PROMOTIONS
-- =========================
CREATE TABLE IF NOT EXISTS tour_promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    promotion_id BIGINT NOT NULL,
    CONSTRAINT uq_tour_promotions UNIQUE (tour_id, promotion_id),
    CONSTRAINT fk_tour_promotions_tour FOREIGN KEY (tour_id) REFERENCES tours (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tour_promotions_promotion FOREIGN KEY (promotion_id) REFERENCES promotions (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =========================
-- 12. BOOKINGS
-- =========================
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    tour_departure_id BIGINT NOT NULL,
    promotion_id BIGINT NULL,
    contact_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    adult_count INT NOT NULL DEFAULT 1,
    child_count INT NOT NULL DEFAULT 0,
    infant_count INT NOT NULL DEFAULT 0,
    total_people INT NOT NULL,
    original_amount DECIMAL(15, 2) NOT NULL,
    discount_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
    final_amount DECIMAL(15, 2) NOT NULL,
    special_requests TEXT,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_tour_departure FOREIGN KEY (tour_departure_id) REFERENCES tour_departures (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_promotion FOREIGN KEY (promotion_id) REFERENCES promotions (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_bookings_counts_adult CHECK (adult_count >= 0),
    CONSTRAINT chk_bookings_counts_child CHECK (child_count >= 0),
    CONSTRAINT chk_bookings_counts_infant CHECK (infant_count >= 0),
    CONSTRAINT chk_bookings_total_people CHECK (total_people >= 1),
    CONSTRAINT chk_bookings_amounts CHECK (
        original_amount >= 0
        AND discount_amount >= 0
        AND final_amount >= 0
    )
);

-- =========================
-- 13. BOOKING TRAVELERS
-- =========================
CREATE TABLE IF NOT EXISTS booking_travelers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    traveler_type VARCHAR(20) NOT NULL,
    identity_number VARCHAR(30),
    nationality VARCHAR(50),
    note VARCHAR(255),
    CONSTRAINT fk_booking_travelers_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =========================
-- 14. PAYMENTS
-- =========================
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    payment_code VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(15, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_type VARCHAR(30) NOT NULL DEFAULT 'FULL',
    transaction_ref VARCHAR(100),
    paid_at DATETIME,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_payments_amount CHECK (amount >= 0)
);

-- =========================
-- 15. REVIEWS
-- =========================
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    tour_id BIGINT NOT NULL,
    rating INT NOT NULL,
    title VARCHAR(150),
    content TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'VISIBLE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_reviews_booking UNIQUE (booking_id),
    CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reviews_tour FOREIGN KEY (tour_id) REFERENCES tours (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

-- =========================
-- SEED: Roles mặc định
-- =========================
INSERT IGNORE INTO roles (name, description) VALUES
    ('ADMIN', 'Quản trị viên hệ thống'),
    ('STAFF', 'Nhân viên'),
    ('CUSTOMER', 'Khách hàng');
