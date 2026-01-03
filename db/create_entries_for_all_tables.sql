-- 2. Insert USERS
-- NOTA: Hash-ul de mai jos este cel generat de tine:
-- $2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK
INSERT INTO users (email, password_hash, first_name, last_name, phone_number, role)
VALUES
-- Admins
('admin@test.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'System', 'Admin', '+15550101', 'admin'),
('sarah.connor@example.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Sarah', 'Connor', '+15550102', 'admin'),

-- Volunteers
('john.doe@example.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'John', 'Doe', '+4477009001', 'volunteer'),
('marie.curie@example.fr', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Marie', 'Curie', '+3312345678', 'volunteer'),
('hans.mueller@example.de', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Hans', 'Mueller', '+4915123456', 'volunteer'),
('yuki.tanaka@example.jp', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Yuki', 'Tanaka', '+8190123456', 'volunteer'),
('sofia.rossi@example.it', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Sofia', 'Rossi', '+3933312345', 'volunteer'),

-- Coordinators
('james.smith@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'James', 'Smith', '+120255501', 'coordinator'),
('elena.popescu@ngo.ro', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Elena', 'Popescu', '+40722123456', 'coordinator'),
('chen.wei@charity.cn', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Chen', 'Wei', '+8613912345', 'coordinator'),
('lucas.silva@ajuda.br', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Lucas', 'Silva', '+5511987654', 'coordinator'),
('amara.okeke@hope.ng', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Amara', 'Okeke', '+2348031234', 'coordinator');


-- 3. Insert ONGS
INSERT INTO ongs (name, description, address, registration_number, phone, email, founding_date)
VALUES
('Global Green Initiative', 'Focusing on reforestation and sustainable agriculture worldwide.', '123 Earth Avenue, London, UK', 'UK-REG-2020-001', '+442071234567', 'contact@globalgreen.org', '2015-04-22'),
('Tech for All', 'Bridging the digital divide by providing laptops and coding lessons.', '456 Silicon Valley, San Francisco, USA', 'US-501C3-9988', '+16505550199', 'info@techforall.org', '2018-09-10'),
('Health Without Borders', 'Medical aid for remote villages and conflict zones.', '789 Rue de la Paix, Paris, France', 'FR-ASSO-1122', '+33140001122', 'support@hwb.fr', '2010-01-15'),
('Education First Romania', 'After-school programs for disadvantaged children.', 'Bulevardul Unirii 10, Bucharest, RO', 'RO-NGO-556677', '+40213334455', 'contact@edufirst.ro', '2012-06-01'),
('Ocean Cleanup Squad', 'Removing plastic waste from oceans and beaches.', '88 Beach Road, Sydney, Australia', 'AU-ABN-778899', '+61298765432', 'volunteer@oceancleanup.com', '2019-11-30');


-- 4. Insert VOLUNTEERS
INSERT INTO volunteers (ID_user, birth_date, skills, availability, emergency_contact)
VALUES
(3, '1995-05-15', 'Teaching, English, Math', 'Weekends', 'Mother: +4477009002'),
(4, '1998-11-20', 'Medical Aid, First Aid, French', 'Mon-Fri Afternoons', 'Husband: +3312345679'),
(5, '2001-03-10', 'Driver, Logistics, German', 'Full-time Summer', 'Father: +4915123457'),
(6, '1990-07-25', 'Coding, Web Design, Japanese', 'Remote only', 'Sister: +8190123457'),
(7, '1999-12-05', 'Cooking, Event Planning, Italian', 'Flexible', 'Brother: +3933312346');


-- 5. Insert COORDINATORS
INSERT INTO coordinators (ID_user, ID_ong, department, experience_years, employment_type)
VALUES
(8, 1, 'Environmental Projects', 5, 'Full-time'),
(9, 4, 'Educational Programs', 8, 'Full-time'),
(10, 2, 'IT Support & Training', 3, 'Part-time'),
(11, 3, 'Field Operations', 10, 'Contract'),
(12, 5, 'Community Outreach', 4, 'Full-time');


-- 6. Insert ACTIVITY CATEGORIES
INSERT INTO activity_categories (name, description)
VALUES
('Environment', 'Tree planting, cleanup, recycling workshops'),
('Education', 'Tutoring, mentoring, language classes'),
('Healthcare', 'Medical camps, health awareness, vaccination drives'),
('Technology', 'Coding bootcamps, digital literacy'),
('Social Welfare', 'Food banks, shelter support, elderly care');


-- 7. Insert ACTIVITIES
INSERT INTO activities (ID_ong, ID_category, ID_coordinator, name, description, location, start_date, end_date, max_volunteers, status)
VALUES
(1, 1, 1, 'London Park Re-wilding', 'Planting native species in city parks.', 'Hyde Park, London', '2026-03-15 09:00:00', '2026-03-15 16:00:00', 50, 'open'),
(2, 4, 3, 'Coding for Kids Workshop', 'Intro to Python for students.', 'Online via Zoom', '2026-04-01 14:00:00', '2026-04-01 18:00:00', 10, 'open'),
(3, 3, 4, 'Rural Health Checkup', 'Mobile clinic for remote villages.', 'Village Square, Lyon Outskirts', '2026-05-10 08:00:00', '2026-05-12 18:00:00', 20, 'planning'),
(4, 2, 2, 'Bucharest Homework Club', 'Helping kids with math homework.', 'School No. 1, Bucharest', '2026-02-20 15:00:00', '2026-06-20 17:00:00', 5, 'open'),
(5, 1, 5, 'Bondi Beach Cleanup', 'Removing microplastics from the sand.', 'Bondi Beach, Sydney', '2026-01-20 07:00:00', '2026-01-20 12:00:00', 100, 'completed');


-- 8. Insert VOLUNTEER ACTIVITIES
INSERT INTO volunteer_activities (ID_volunteer, ID_activity, enrollment_date, status, hours_completed, feedback)
VALUES
(1, 1, '2026-03-01', 'accepted', 0, NULL),
(1, 2, '2026-03-10', 'pending', 0, NULL),
(2, 3, '2026-04-05', 'accepted', 0, NULL),
(3, 1, '2026-03-02', 'rejected', 0, 'Unavailable'),
(4, 2, '2026-03-12', 'accepted', 4, 'Great event!'),
(5, 5, '2026-01-10', 'completed', 5, 'Very hot day but rewarding');


-- 9. Insert DONATIONS
INSERT INTO donations (ID_ong, donor_name, amount, donation_date, type, notes)
VALUES
(1, 'EcoCorp Inc.', 5000.00, '2025-12-15', 'Corporate', 'Annual sponsorship'),
(2, 'Anonymous', 50.00, '2026-01-05', 'Individual', 'Online donation'),
(3, 'Global Health Fund', 12000.00, '2025-11-20', 'Grant', 'For equipment purchase'),
(4, 'Local Bakery', 200.00, '2026-02-01', 'In-Kind', 'Food for Homework Club'),
(5, 'Surfers United', 750.00, '2026-01-18', 'Event', 'Fundraiser at the beach');