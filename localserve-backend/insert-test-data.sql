-- ============================================================================
-- LOCALSERVE TEST DATA SQL SCRIPT
-- ============================================================================
-- This script populates the database with comprehensive test data for all tables
-- Run this ONCE after the tables are created by Hibernate
-- ============================================================================

-- Step 1: Clean existing data (Optional - uncomment if needed for PostgreSQL)
-- DELETE FROM bookings;
-- DELETE FROM service_offerings;
-- DELETE FROM providers;
-- DELETE FROM users;
-- DELETE FROM master_service_categories;
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE providers_id_seq RESTART WITH 1;
-- ALTER SEQUENCE master_service_categories_id_seq RESTART WITH 1;
-- ALTER SEQUENCE service_offerings_id_seq RESTART WITH 1;
-- ALTER SEQUENCE bookings_id_seq RESTART WITH 1;

-- ============================================================================
-- TABLE 1: MASTER_SERVICE_CATEGORIES
-- ============================================================================
-- These are the main service types available in the system
INSERT INTO master_service_categories (name) VALUES
('Plumbing'),
('Electrical'),
('Carpentry'),
('House Cleaning'),
('Painting'),
('HVAC'),
('Landscaping'),
('Locksmith'),
('General Repair'),
('Home Inspection');

-- ============================================================================
-- TABLE 2: USERS (Regular Users - Non-Providers)
-- ============================================================================
-- Passwords are hashed - these are test passwords hashed with BCrypt
-- Test credentials format: email / password
INSERT INTO users (name, email, password, role, phone, latitude, longitude, created_at) VALUES
-- Regular users who can book services
('John Doe', 'john@gmail.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0001', 40.7128, -74.0060, NOW()),
('Jane Smith', 'jane@gmail.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0002', 40.7549, -73.9840, NOW()),
('Mike Johnson', 'mike@gmail.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0003', 40.7614, -73.9776, NOW()),
('Sarah Williams', 'sarah@gmail.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0004', 40.7505, -73.9934, NOW()),
('Robert Brown', 'robert@gmail.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0005', 40.7580, -73.9855, NOW());

-- ============================================================================
-- TABLE 3: USERS (Provider Users)
-- ============================================================================
-- These users have PROVIDER role and can offer services
INSERT INTO users (name, email, password, role, phone, latitude, longitude, created_at) VALUES
-- Providers
('John Plumber', 'john.plumber@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1001', 40.7200, -74.0100, NOW()),
('Sarah Electrician', 'sarah.electric@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1002', 40.7300, -73.9900, NOW()),
('Mike Carpenter', 'mike.carpenter@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1003', 40.7450, -73.9850, NOW()),
('Lisa Painter', 'lisa.painter@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1004', 40.7600, -73.9750, NOW()),
('David HVAC', 'david.hvac@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1005', 40.7100, -74.0200, NOW()),
('Emma Cleaner', 'emma.cleaning@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1006', 40.7400, -73.9700, NOW()),
('Tom Landscaper', 'tom.landscape@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1007', 40.7700, -73.9600, NOW()),
('Alex Locksmith', 'alex.locksmith@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1008', 40.7350, -74.0050, NOW()),
('Chris Handyman', 'chris.handyman@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1009', 40.7500, -73.9900, NOW()),
('Nina Inspector', 'nina.inspector@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1010', 40.7250, -73.9950, NOW());

-- ============================================================================
-- TABLE 4: PROVIDERS
-- ============================================================================
-- Creates provider profiles for the provider users
-- User IDs 6-15 are the providers created above
INSERT INTO providers (user_id, business_name, description, experience_years, service_radius, rating, location, latitude, longitude) VALUES
(6, 'John Plumbing Services', 'Expert plumbing services for residential and commercial properties. 24/7 emergency service available.', 10, 25, 4.8, '123 Main St, New York, NY', 40.7200, -74.0100),
(7, 'Sarah Electric Works', 'Licensed electrician providing installation, repair, and maintenance services. Fully insured.', 8, 20, 4.9, '456 Park Ave, New York, NY', 40.7300, -73.9900),
(8, 'Mike Carpentry Solutions', 'Custom woodwork, furniture repair, and home renovation carpentry services.', 12, 30, 4.7, '789 Elm St, New York, NY', 40.7450, -73.9850),
(9, 'Creative Painting Co.', 'Interior and exterior painting with premium paint and professional finishes.', 7, 15, 4.6, '321 Broadway, New York, NY', 40.7600, -73.9750),
(10, 'David HVAC Experts', 'Air conditioning, heating, and ventilation system installation and repair.', 15, 35, 4.9, '654 5th Ave, New York, NY', 40.7100, -74.0200),
(11, 'Emma Professional Cleaning', 'Residential and commercial cleaning services. Eco-friendly products used.', 5, 20, 4.8, '987 Madison Ave, New York, NY', 40.7400, -73.9700),
(12, 'Tom Garden & Landscape Design', 'Landscaping, garden design, and outdoor maintenance services.', 9, 40, 4.7, '147 Wall St, New York, NY', 40.7700, -73.9600),
(13, 'Alex Express Locksmith', 'Commercial and residential lock repair, replacement, and emergency lockout services.', 11, 25, 4.9, '258 Lexington Ave, New York, NY', 40.7350, -74.0050),
(14, 'Chris All-Around Handyman', 'General repairs, home maintenance, and minor renovations.', 6, 18, 4.5, '369 Park Pl, New York, NY', 40.7500, -73.9900),
(15, 'Nina Home Inspections', 'Professional home inspection and property assessment services for buyers and sellers.', 13, 50, 4.9, '741 Herald Sq, New York, NY', 40.7250, -73.9950);

-- ============================================================================
-- TABLE 5: SERVICE_OFFERINGS
-- ============================================================================
-- Links providers to service categories (what services they offer)
-- Provider IDs 1-10 from providers table
INSERT INTO service_offerings (service_category_id, provider_id) VALUES
-- John Plumbing offers Plumbing
(1, 1),
-- Sarah Electric offers Electrical
(2, 2),
-- Mike Carpenter offers Carpentry
(3, 3),
-- Lisa Painter offers Painting
(5, 4),
-- David HVAC offers HVAC
(6, 5),
-- Emma Cleaner offers House Cleaning
(4, 6),
-- Tom Landscaper offers Landscaping
(7, 7),
-- Alex Locksmith offers Locksmith
(8, 8),
-- Chris Handyman offers General Repair
(9, 9),
-- Nina Inspector offers Home Inspection
(10, 10),
-- Multiple offerings for some providers
(1, 9), -- Chris also does plumbing repair
(4, 6), -- Emma also does move-out cleaning
(5, 4), -- Lisa also does touch-ups
(8, 9), -- Chris also installs locks
(9, 3); -- Mike also does general repairs

-- ============================================================================
-- TABLE 6: BOOKINGS
-- ============================================================================
-- Creates bookings between users and providers
-- User IDs 1-5 are regular users, Provider IDs 1-10 from providers table
INSERT INTO bookings (user_id, provider_id, service_offering_id, status, booking_time, created_at, updated_at) VALUES
-- John (User 1) books with providers
(1, 1, 1, 'COMPLETED', '2024-04-01 10:00:00', '2024-03-30 09:00:00', '2024-04-01 11:00:00'),
(1, 2, 2, 'ACCEPTED', '2024-04-10 14:00:00', '2024-04-08 08:00:00', '2024-04-09 10:00:00'),
(1, 6, 11, 'REQUESTED', '2024-04-15 09:00:00', '2024-04-14 15:00:00', '2024-04-14 15:00:00'),

-- Jane (User 2) books with providers
(2, 3, 3, 'COMPLETED', '2024-03-25 13:00:00', '2024-03-20 10:00:00', '2024-03-25 14:00:00'),
(2, 4, 13, 'COMPLETED', '2024-04-02 11:00:00', '2024-03-31 09:00:00', '2024-04-02 12:00:00'),
(2, 5, 6, 'ACCEPTED', '2024-04-12 15:00:00', '2024-04-10 12:00:00', '2024-04-11 14:00:00'),
(2, 10, 10, 'REQUESTED', '2024-04-20 10:00:00', '2024-04-19 16:00:00', '2024-04-19 16:00:00'),

-- Mike (User 3) books with providers
(3, 7, 7, 'COMPLETED', '2024-03-15 09:00:00', '2024-03-10 08:00:00', '2024-03-15 10:00:00'),
(3, 8, 8, 'ACCEPTED', '2024-04-08 16:00:00', '2024-04-06 11:00:00', '2024-04-07 15:00:00'),
(3, 9, 15, 'REJECTED', '2024-04-05 10:00:00', '2024-04-04 14:00:00', '2024-04-05 11:00:00'),
(3, 1, 1, 'REQUESTED', '2024-04-18 11:00:00', '2024-04-17 10:00:00', '2024-04-17 10:00:00'),

-- Sarah (User 4) books with providers
(4, 2, 2, 'COMPLETED', '2024-03-20 14:00:00', '2024-03-18 10:00:00', '2024-03-20 15:00:00'),
(4, 6, 4, 'COMPLETED', '2024-04-01 10:00:00', '2024-03-29 13:00:00', '2024-04-01 11:00:00'),
(4, 4, 5, 'ACCEPTED', '2024-04-14 13:00:00', '2024-04-12 09:00:00', '2024-04-13 11:00:00'),

-- Robert (User 5) books with providers
(5, 3, 3, 'COMPLETED', '2024-03-28 12:00:00', '2024-03-26 10:00:00', '2024-03-28 13:00:00'),
(5, 5, 6, 'COMPLETED', '2024-04-05 15:00:00', '2024-04-03 08:00:00', '2024-04-05 16:00:00'),
(5, 8, 8, 'REQUESTED', '2024-04-22 10:00:00', '2024-04-21 17:00:00', '2024-04-21 17:00:00'),
(5, 9, 9, 'ACCEPTED', '2024-04-16 14:00:00', '2024-04-15 12:00:00', '2024-04-15 14:00:00');

-- ============================================================================
-- SUMMARY
-- ============================================================================
-- Regular Users: 5 (ID 1-5)
-- Providers: 10 (ID 6-15)
-- Service Categories: 10
-- Service Offerings: 15
-- Bookings: 19
--
-- Test Login Credentials:
-- Regular User:
--   Email: john@gmail.com
--   Password: password123
--
-- Provider User:
--   Email: john.plumber@email.com
--   Password: password123
--
-- ============================================================================


