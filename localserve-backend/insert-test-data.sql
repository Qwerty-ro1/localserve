-- ============================================================================
-- LOCALSERVE TEST DATA SQL SCRIPT (UPDATED)
-- ============================================================================

-- ============================================================================
-- Step 1: Clean existing data
-- ============================================================================
DELETE FROM bookings;
DELETE FROM user_addresses;
DELETE FROM service_offerings;
DELETE FROM providers;
DELETE FROM users;
DELETE FROM master_service_categories;

ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE providers_id_seq RESTART WITH 1;
ALTER SEQUENCE master_service_categories_id_seq RESTART WITH 1;
ALTER SEQUENCE service_offerings_id_seq RESTART WITH 1;
ALTER SEQUENCE bookings_id_seq RESTART WITH 1;
ALTER SEQUENCE user_addresses_id_seq RESTART WITH 1;

-- ============================================================================
-- TABLE 1: MASTER_SERVICE_CATEGORIES
-- ============================================================================
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
-- TABLE 2: USERS (Regular Users)
-- ============================================================================
-- removed latitude, longitude columns
-- password123 for all
INSERT INTO users (name, email, password, role, phone, created_at) VALUES
('John Doe',       'john@gmail.com',   '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0001', NOW()),
('Jane Smith',     'jane@gmail.com',   '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0002', NOW()),
('Mike Johnson',   'mike@gmail.com',   '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0003', NOW()),
('Sarah Williams', 'sarah@gmail.com',  '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0004', NOW()),
('Robert Brown',   'robert@gmail.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'USER', '555-0005', NOW());

-- ============================================================================
-- TABLE 3: USERS (Providers)
-- ============================================================================
INSERT INTO users (name, email, password, role, phone, created_at) VALUES
('John Plumber',    'john.plumber@email.com',   '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1001', NOW()),
('Sarah Electric',  'sarah.electric@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1002', NOW()),
('Mike Carpenter',  'mike.carpenter@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1003', NOW()),
('Lisa Painter',    'lisa.painter@email.com',   '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1004', NOW()),
('David HVAC',      'david.hvac@email.com',     '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1005', NOW()),
('Emma Cleaner',    'emma.cleaning@email.com',  '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1006', NOW()),
('Tom Landscaper',  'tom.landscape@email.com',  '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1007', NOW()),
('Alex Locksmith',  'alex.locksmith@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1008', NOW()),
('Chris Handyman',  'chris.handyman@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1009', NOW()),
('Nina Inspector',  'nina.inspector@email.com', '$2a$10$5HayNagrSfRPTXBpCVfd3.rFY3fjI1SahbpWGnxqIUAPaaG468NPq', 'PROVIDER', '555-1010', NOW());

-- ============================================================================
-- TABLE 4: PROVIDERS
-- ============================================================================
-- user IDs 6-15 are providers
INSERT INTO providers (user_id, business_name, description, experience_years, service_radius, rating, location, latitude, longitude) VALUES
(6,  'John Plumbing Services',      'Expert plumbing for residential and commercial. 24/7 emergency service.',         10, 25, 4.8, '123 Main St, New York, NY',      40.7200, -74.0100),
(7,  'Sarah Electric Works',        'Licensed electrician for installation, repair, and maintenance. Fully insured.',  8,  20, 4.9, '456 Park Ave, New York, NY',     40.7300, -73.9900),
(8,  'Mike Carpentry Solutions',    'Custom woodwork, furniture repair, and home renovation carpentry.',               12, 30, 4.7, '789 Elm St, New York, NY',       40.7450, -73.9850),
(9,  'Creative Painting Co.',       'Interior and exterior painting with premium paint and professional finishes.',    7,  15, 4.6, '321 Broadway, New York, NY',     40.7600, -73.9750),
(10, 'David HVAC Experts',          'AC, heating, and ventilation system installation and repair.',                    15, 35, 4.9, '654 5th Ave, New York, NY',      40.7100, -74.0200),
(11, 'Emma Professional Cleaning',  'Residential and commercial cleaning. Eco-friendly products.',                    5,  20, 4.8, '987 Madison Ave, New York, NY',  40.7400, -73.9700),
(12, 'Tom Garden & Landscape',      'Landscaping, garden design, and outdoor maintenance.',                           9,  40, 4.7, '147 Wall St, New York, NY',      40.7700, -73.9600),
(13, 'Alex Express Locksmith',      'Lock repair, replacement, and emergency lockout services.',                      11, 25, 4.9, '258 Lexington Ave, New York, NY',40.7350, -74.0050),
(14, 'Chris All-Around Handyman',   'General repairs, home maintenance, and minor renovations.',                      6,  18, 4.5, '369 Park Pl, New York, NY',      40.7500, -73.9900),
(15, 'Nina Home Inspections',       'Professional home inspection and property assessment.',                          13, 50, 4.9, '741 Herald Sq, New York, NY',    40.7250, -73.9950);

-- ============================================================================
-- TABLE 5: SERVICE_OFFERINGS
-- ============================================================================
INSERT INTO service_offerings (service_category_id, provider_id) VALUES
(1, 1),  -- John Plumbing → Plumbing
(2, 2),  -- Sarah Electric → Electrical
(3, 3),  -- Mike Carpenter → Carpentry
(5, 4),  -- Lisa Painter → Painting
(6, 5),  -- David HVAC → HVAC
(4, 6),  -- Emma Cleaner → House Cleaning
(7, 7),  -- Tom Landscaper → Landscaping
(8, 8),  -- Alex Locksmith → Locksmith
(9, 9),  -- Chris Handyman → General Repair
(10,10), -- Nina Inspector → Home Inspection
(1, 9),  -- Chris also does Plumbing repair
(4, 6),  -- Emma also does move-out cleaning
(5, 4),  -- Lisa also does touch-ups
(8, 9),  -- Chris also installs locks
(9, 3);  -- Mike also does general repairs

-- ============================================================================
-- TABLE 6: USER_ADDRESSES
-- ============================================================================
-- Each regular user gets 2 addresses (Home + Office)
-- using NYC coords to match provider test data
-- user IDs 1-5 are regular users
INSERT INTO user_addresses (user_id, label, address_line, latitude, longitude, is_default) VALUES
-- John Doe (user 1)
(1, 'HOME',   '10 East 21st St, New York, NY',     40.7128, -74.0060, true),
(1, 'OFFICE', '350 Fifth Ave, New York, NY',        40.7484, -73.9967, false),

-- Jane Smith (user 2)
(2, 'HOME',   '245 W 72nd St, New York, NY',        40.7549, -73.9840, true),
(2, 'OFFICE', '1221 Ave of Americas, New York, NY', 40.7587, -73.9787, false),

-- Mike Johnson (user 3)
(3, 'HOME',   '160 Columbus Ave, New York, NY',     40.7614, -73.9776, true),
(3, 'OFFICE', '30 Rockefeller Plaza, New York, NY', 40.7587, -73.9787, false),

-- Sarah Williams (user 4)
(4, 'HOME',   '400 W 34th St, New York, NY',        40.7505, -73.9934, true),
(4, 'OFFICE', '11 Penn Plaza, New York, NY',         40.7497, -73.9933, false),

-- Robert Brown (user 5)
(5, 'HOME',   '175 E 96th St, New York, NY',        40.7580, -73.9855, true),
(5, 'OTHER',  '55 Water St, New York, NY',           40.7033, -74.0107, false);

-- ============================================================================
-- TABLE 7: BOOKINGS
-- ============================================================================
-- service_address_id references user_addresses
-- John's addresses: id 1 (home), 2 (office)
-- Jane's addresses: id 3 (home), 4 (office)
-- Mike's addresses: id 5 (home), 6 (office)
-- Sarah's addresses: id 7 (home), 8 (office)
-- Robert's addresses: id 9 (home), 10 (other)

INSERT INTO bookings (user_id, provider_id, service_offering_id, service_address_id, status, booking_time, created_at, updated_at) VALUES
-- John (user 1)
(1, 1, 1,  1, 'COMPLETED', '2024-04-01 10:00:00', '2024-03-30 09:00:00', '2024-04-01 11:00:00'),
(1, 2, 2,  2, 'ACCEPTED',  '2024-04-10 14:00:00', '2024-04-08 08:00:00', '2024-04-09 10:00:00'),
(1, 6, 11, 1, 'REQUESTED', '2024-04-15 09:00:00', '2024-04-14 15:00:00', '2024-04-14 15:00:00'),

-- Jane (user 2)
(2, 3, 3,  3, 'COMPLETED', '2024-03-25 13:00:00', '2024-03-20 10:00:00', '2024-03-25 14:00:00'),
(2, 4, 13, 4, 'COMPLETED', '2024-04-02 11:00:00', '2024-03-31 09:00:00', '2024-04-02 12:00:00'),
(2, 5, 6,  3, 'ACCEPTED',  '2024-04-12 15:00:00', '2024-04-10 12:00:00', '2024-04-11 14:00:00'),
(2, 10,10, 4, 'REQUESTED', '2024-04-20 10:00:00', '2024-04-19 16:00:00', '2024-04-19 16:00:00'),

-- Mike (user 3)
(3, 7, 7,  5, 'COMPLETED', '2024-03-15 09:00:00', '2024-03-10 08:00:00', '2024-03-15 10:00:00'),
(3, 8, 8,  6, 'ACCEPTED',  '2024-04-08 16:00:00', '2024-04-06 11:00:00', '2024-04-07 15:00:00'),
(3, 9, 15, 5, 'REJECTED',  '2024-04-05 10:00:00', '2024-04-04 14:00:00', '2024-04-05 11:00:00'),
(3, 1, 1,  5, 'REQUESTED', '2024-04-18 11:00:00', '2024-04-17 10:00:00', '2024-04-17 10:00:00'),

-- Sarah (user 4)
(4, 2, 2,  7, 'COMPLETED', '2024-03-20 14:00:00', '2024-03-18 10:00:00', '2024-03-20 15:00:00'),
(4, 6, 4,  8, 'COMPLETED', '2024-04-01 10:00:00', '2024-03-29 13:00:00', '2024-04-01 11:00:00'),
(4, 4, 5,  7, 'ACCEPTED',  '2024-04-14 13:00:00', '2024-04-12 09:00:00', '2024-04-13 11:00:00'),

-- Robert (user 5)
(5, 3, 3,  9,  'COMPLETED', '2024-03-28 12:00:00', '2024-03-26 10:00:00', '2024-03-28 13:00:00'),
(5, 5, 6,  10, 'COMPLETED', '2024-04-05 15:00:00', '2024-04-03 08:00:00', '2024-04-05 16:00:00'),
(5, 8, 8,  9,  'REQUESTED', '2024-04-22 10:00:00', '2024-04-21 17:00:00', '2024-04-21 17:00:00'),
(5, 9, 9,  10, 'ACCEPTED',  '2024-04-16 14:00:00', '2024-04-15 12:00:00', '2024-04-15 14:00:00');

-- ============================================================================
-- SUMMARY
-- ============================================================================
-- Regular Users : 5  (ID 1-5)
-- Provider Users: 10 (ID 6-15)
-- Providers     : 10 (ID 1-10)
-- Categories    : 10
-- Offerings     : 15
-- Addresses     : 10 (2 per user)
-- Bookings      : 19
--
-- Test credentials (password: password123)
-- Regular : john@gmail.com
-- Provider: john.plumber@email.com
-- ============================================================================