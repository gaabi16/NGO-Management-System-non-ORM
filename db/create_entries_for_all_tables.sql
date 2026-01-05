-- 1. Insert USERS
INSERT INTO users (email, password_hash, first_name, last_name, phone_number, role) VALUES
('admin@test.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'System', 'Admin', '+15550101', 'admin'),
('sarah.connor@example.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Sarah', 'Connor', '+15550102', 'admin'),
('john.doe@example.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'John', 'Doe', '+4477009001', 'volunteer'),
('marie.curie@example.fr', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Marie', 'Curie', '+3312345678', 'volunteer'),
('hans.mueller@example.de', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Hans', 'Mueller', '+4915123456', 'volunteer'),
('yuki.tanaka@example.jp', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Yuki', 'Tanaka', '+8190123456', 'volunteer'),
('sofia.rossi@example.it', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Sofia', 'Rossi', '+3933312345', 'volunteer'),
('james.smith@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'James', 'Smith', '+120255501', 'coordinator'),
('elena.popescu@ngo.ro', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Elena', 'Popescu', '+40722123456', 'coordinator'),
('chen.wei@charity.cn', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Chen', 'Wei', '+8613912345', 'coordinator'),
('lucas.silva@ajuda.br', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Lucas', 'Silva', '+5511987654', 'coordinator'),
('amara.okeke@hope.ng', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Amara', 'Okeke', '+2348031234', 'coordinator');

-- 2. Insert ONGS (PK String, added Country)
INSERT INTO ongs (registration_number, name, description, address, country, phone, email, founding_date)
VALUES
('UK-REG-2020-001', 'Global Green Initiative', 'Focusing on reforestation.', '123 Earth Avenue, London', 'UK', '+442071234567', 'contact@globalgreen.org', '2015-04-22'),
('US-501C3-9988', 'Tech for All', 'Bridging the digital divide.', '456 Silicon Valley, San Francisco', 'USA', '+16505550199', 'info@techforall.org', '2018-09-10'),
('FR-ASSO-1122', 'Health Without Borders', 'Medical aid for remote villages.', '789 Rue de la Paix, Paris', 'France', '+33140001122', 'support@hwb.fr', '2010-01-15'),
('RO-NGO-556677', 'Education First Romania', 'After-school programs.', 'Bulevardul Unirii 10, Bucharest', 'Romania', '+40213334455', 'contact@edufirst.ro', '2012-06-01'),
('AU-ABN-778899', 'Ocean Cleanup Squad', 'Removing plastic waste.', '88 Beach Road, Sydney', 'Australia', '+61298765432', 'volunteer@oceancleanup.com', '2019-11-30');

-- 3. Insert VOLUNTEERS
INSERT INTO volunteers (ID_user, birth_date, skills, availability, emergency_contact)
VALUES
(3, '1995-05-15', 'Teaching, English, Math', 'Weekends', 'Mother: +4477009002'),
(4, '1998-11-20', 'Medical Aid, First Aid, French', 'Mon-Fri Afternoons', 'Husband: +3312345679'),
(5, '2001-03-10', 'Driver, Logistics, German', 'Full-time Summer', 'Father: +4915123457'),
(6, '1990-07-25', 'Coding, Web Design, Japanese', 'Remote only', 'Sister: +8190123457'),
(7, '1999-12-05', 'Cooking, Event Planning, Italian', 'Flexible', 'Brother: +3933312346');

-- 4. Insert COORDINATORS (Uses String FK)
INSERT INTO coordinators (ID_user, ong_registration_number, department, experience_years, employment_type)
VALUES
(8, 'UK-REG-2020-001', 'Environmental Projects', 5, 'Full-time'),
(9, 'RO-NGO-556677', 'Educational Programs', 8, 'Full-time'),
(10, 'US-501C3-9988', 'IT Support & Training', 3, 'Part-time'),
(11, 'FR-ASSO-1122', 'Field Operations', 10, 'Contract'),
(12, 'AU-ABN-778899', 'Community Outreach', 4, 'Full-time');

-- 5. Insert ACTIVITY CATEGORIES
INSERT INTO activity_categories (name, description)
VALUES
('Environment', 'Tree planting, cleanup'),
('Education', 'Tutoring, mentoring'),
('Healthcare', 'Medical camps'),
('Technology', 'Coding bootcamps'),
('Social Welfare', 'Food banks');

-- 6. Insert ACTIVITIES (Removed ONG FK, added donations_collected)
-- Note: ONG is inferred from Coordinator (ID_coordinator)
INSERT INTO activities (ID_category, ID_coordinator, name, description, location, start_date, end_date, max_volunteers, status, donations_collected)
VALUES
(1, 1, 'London Park Re-wilding', 'Planting native species.', 'Hyde Park, London', '2026-03-15 09:00:00', '2026-03-15 16:00:00', 50, 'open', 1500.00),
(4, 3, 'Coding for Kids Workshop', 'Intro to Python.', 'Online via Zoom', '2026-04-01 14:00:00', '2026-04-01 18:00:00', 10, 'open', 300.50),
(3, 4, 'Rural Health Checkup', 'Mobile clinic.', 'Village Square, Lyon', '2026-05-10 08:00:00', '2026-05-12 18:00:00', 20, 'planning', 5000.00),
(2, 2, 'Bucharest Homework Club', 'Math homework help.', 'School No. 1, Bucharest', '2026-02-20 15:00:00', '2026-06-20 17:00:00', 5, 'open', 100.00),
(1, 5, 'Bondi Beach Cleanup', 'Removing microplastics.', 'Bondi Beach, Sydney', '2026-01-20 07:00:00', '2026-01-20 12:00:00', 100, 'completed', 0.00);

-- 7. Insert VOLUNTEER ACTIVITIES
INSERT INTO volunteer_activities (ID_volunteer, ID_activity, enrollment_date, status, hours_completed, feedback)
VALUES
(1, 1, '2026-03-01', 'accepted', 0, NULL),
(1, 2, '2026-03-10', 'pending', 0, NULL),
(2, 3, '2026-04-05', 'accepted', 0, NULL),
(3, 1, '2026-03-02', 'rejected', 0, 'Unavailable'),
(4, 2, '2026-03-12', 'accepted', 4, 'Great event!'),
(5, 5, '2026-01-10', 'completed', 5, 'Very hot day');

-- 8. Insert DONATIONS (Uses String FK)
INSERT INTO donations (ong_registration_number, donor_name, amount, donation_date, type, notes)
VALUES
('UK-REG-2020-001', 'EcoCorp Inc.', 5000.00, '2025-12-15', 'Corporate', 'Annual sponsorship'),
('US-501C3-9988', 'Anonymous', 50.00, '2026-01-05', 'Individual', 'Online donation'),
('FR-ASSO-1122', 'Global Health Fund', 12000.00, '2025-11-20', 'Grant', 'For equipment'),
('RO-NGO-556677', 'Local Bakery', 200.00, '2026-02-01', 'In-Kind', 'Food'),
('AU-ABN-778899', 'Surfers United', 750.00, '2026-01-18', 'Event', 'Fundraiser');