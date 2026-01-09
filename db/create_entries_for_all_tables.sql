-- Stergem datele vechi (optional, pentru siguranta)
TRUNCATE TABLE volunteer_activities, activities, donations, coordinators, volunteers, activity_categories, ongs, users RESTART IDENTITY CASCADE;

-- 1. Insert USERS (Total: 55 users)
-- ID 1-2: Admins
-- ID 3-27: Volunteers (25 volunteers)
-- ID 28-55: Coordinators (28 coordinators)
INSERT INTO users (email, password_hash, first_name, last_name, phone_number, role) VALUES
-- Admins
('admin@test.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'System', 'Admin', '+15550101', 'admin'),
('sarah.connor@example.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Sarah', 'Connor', '+15550102', 'admin'),

-- Volunteers (Originals)
('john.doe@example.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'John', 'Doe', '+4477009001', 'volunteer'),
('marie.curie@example.fr', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Marie', 'Curie', '+3312345678', 'volunteer'),
('hans.mueller@example.de', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Hans', 'Mueller', '+4915123456', 'volunteer'),
('yuki.tanaka@example.jp', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Yuki', 'Tanaka', '+8190123456', 'volunteer'),
('sofia.rossi@example.it', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Sofia', 'Rossi', '+3933312345', 'volunteer'),

-- Volunteers (New - 20 entries)
('alice.wonder@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Alice', 'Wonder', '+1200300400', 'volunteer'),
('bob.builder@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Bob', 'Builder', '+1200300401', 'volunteer'),
('charlie.brown@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Charlie', 'Brown', '+1200300402', 'volunteer'),
('diana.prince@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Diana', 'Prince', '+1200300403', 'volunteer'),
('evan.wright@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Evan', 'Wright', '+1200300404', 'volunteer'),
('fiona.shrek@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Fiona', 'Ogre', '+1200300405', 'volunteer'),
('george.jetson@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'George', 'Jetson', '+1200300406', 'volunteer'),
('harry.potter@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Harry', 'Potter', '+4477009009', 'volunteer'),
('iris.west@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Iris', 'West', '+1200300408', 'volunteer'),
('jack.sparrow@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Jack', 'Sparrow', '+1200300409', 'volunteer'),
('kyle.broflovski@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Kyle', 'Broflovski', '+1200300410', 'volunteer'),
('lara.croft@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Lara', 'Croft', '+1200300411', 'volunteer'),
('mario.bros@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Mario', 'Mario', '+3933312399', 'volunteer'),
('nathan.drake@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Nathan', 'Drake', '+1200300413', 'volunteer'),
('olivia.benson@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Olivia', 'Benson', '+1200300414', 'volunteer'),
('peter.parker@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Peter', 'Parker', '+1200300415', 'volunteer'),
('quinn.harley@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Harley', 'Quinn', '+1200300416', 'volunteer'),
('rachel.green@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Rachel', 'Green', '+1200300417', 'volunteer'),
('steve.rogers@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Steve', 'Rogers', '+1200300418', 'volunteer'),
('tony.stark@mail.com', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Tony', 'Stark', '+1200300419', 'volunteer'),

-- Coordinators (Originals - IDs 28-32)
('james.smith@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'James', 'Smith', '+120255501', 'coordinator'),
('elena.popescu@ngo.ro', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Elena', 'Popescu', '+40722123456', 'coordinator'),
('chen.wei@charity.cn', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Chen', 'Wei', '+8613912345', 'coordinator'),
('lucas.silva@ajuda.br', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Lucas', 'Silva', '+5511987654', 'coordinator'),
('amara.okeke@hope.ng', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Amara', 'Okeke', '+2348031234', 'coordinator'),

-- Coordinators (New - 23 entries - IDs 33-55)
('coord1@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Michael', 'Scott', '+15550001', 'coordinator'),
('coord2@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Pam', 'Beesly', '+15550002', 'coordinator'),
('coord3@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Jim', 'Halpert', '+15550003', 'coordinator'),
('coord4@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Dwight', 'Schrute', '+15550004', 'coordinator'),
('coord5@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Leslie', 'Knope', '+15550005', 'coordinator'),
('coord6@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Ron', 'Swanson', '+15550006', 'coordinator'),
('coord7@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'April', 'Ludgate', '+15550007', 'coordinator'),
('coord8@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Andy', 'Dwyer', '+15550008', 'coordinator'),
('coord9@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Tom', 'Haverford', '+15550009', 'coordinator'),
('coord10@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Ann', 'Perkins', '+15550010', 'coordinator'),
('coord11@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Chris', 'Traeger', '+15550011', 'coordinator'),
('coord12@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Ben', 'Wyatt', '+15550012', 'coordinator'),
('coord13@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Jerry', 'Gergich', '+15550013', 'coordinator'),
('coord14@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Donna', 'Meagle', '+15550014', 'coordinator'),
('coord15@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Jake', 'Peralta', '+15550015', 'coordinator'),
('coord16@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Amy', 'Santiago', '+15550016', 'coordinator'),
('coord17@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Rosa', 'Diaz', '+15550017', 'coordinator'),
('coord18@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Terry', 'Jeffords', '+15550018', 'coordinator'),
('coord19@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Charles', 'Boyle', '+15550019', 'coordinator'),
('coord20@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Raymond', 'Holt', '+15550020', 'coordinator'),
('coord21@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Gina', 'Linetti', '+15550021', 'coordinator'),
('coord22@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Michael', 'Hitchcock', '+15550022', 'coordinator'),
('coord23@ong.org', '$2a$10$mYvrSeHifPkY3VnAw5beAuuhx7DKc8jzSImJzNQ2Fk6iF5jBCquMK', 'Norm', 'Scully', '+15550023', 'coordinator');


-- 2. Insert ONGS (Total: 25 ONGs)
INSERT INTO ongs (registration_number, name, description, address, country, phone, email, founding_date)
VALUES
-- Originals
('UK-REG-2020-001', 'Global Green Initiative', 'Focusing on reforestation.', '123 Earth Avenue, London', 'UK', '+442071234567', 'contact@globalgreen.org', '2015-04-22'),
('US-501C3-9988', 'Tech for All', 'Bridging the digital divide.', '456 Silicon Valley, San Francisco', 'USA', '+16505550199', 'info@techforall.org', '2018-09-10'),
('FR-ASSO-1122', 'Health Without Borders', 'Medical aid for remote villages.', '789 Rue de la Paix, Paris', 'France', '+33140001122', 'support@hwb.fr', '2010-01-15'),
('RO-NGO-556677', 'Education First Romania', 'After-school programs.', 'Bulevardul Unirii 10, Bucharest', 'Romania', '+40213334455', 'contact@edufirst.ro', '2012-06-01'),
('AU-ABN-778899', 'Ocean Cleanup Squad', 'Removing plastic waste.', '88 Beach Road, Sydney', 'Australia', '+61298765432', 'volunteer@oceancleanup.com', '2019-11-30'),

-- New entries (20)
('DE-NGO-1001', 'Kinder Hilfe', 'Support for children in need.', 'Berlin Strasse 1', 'Germany', '+4930123456', 'info@kinderhilfe.de', '2005-05-05'),
('IT-ONLUS-2002', 'Cultura Viva', 'Preserving cultural heritage.', 'Via Roma 100, Rome', 'Italy', '+3906123456', 'contact@cultura.it', '2011-02-20'),
('ES-ONG-3003', 'Vida Animal', 'Animal shelter and adoption.', 'Madrid Plaza 5', 'Spain', '+3491123456', 'hola@vidaanimal.es', NULL), -- FOUNDING_DATE NULL
('JP-NPO-4004', 'Elderly Care Japan', 'Assisting the elderly.', 'Tokyo Central 1-1', 'Japan', '+81312345678', 'help@elderly.jp', '1998-10-10'),
('CA-CHARITY-5005', 'Winter Warmth', 'Providing shelter in winter.', 'Toronto Main St 50', 'Canada', '+14161234567', 'warmth@canada.org', '2015-11-01'),
('BR-NGO-6006', 'Amazonia Verde', 'Protecting the rainforest.', 'Manaus Centro', 'Brazil', '+559212345678', 'amazonia@verde.br', '2000-01-01'),
('IN-NGO-7007', 'Clean Water India', 'Sanitation and water projects.', 'Mumbai High St', 'India', '+912212345678', 'water@india.org', '2013-03-22'),
('ZA-NPO-8008', 'Ubuntu Youth', 'Youth empowerment.', 'Cape Town Loop St', 'South Africa', '+27211234567', 'youth@ubuntu.za', '2016-06-16'),
('MX-NGO-9009', 'Comida Para Todos', 'Food bank.', 'Mexico City Reforma', 'Mexico', '+525512345678', 'comida@mx.org', NULL), -- FOUNDING_DATE NULL
('NL-ANBI-1010', 'Bike City', 'Promoting cycling safety.', 'Amsterdam Canal 5', 'Netherlands', '+31201234567', 'bike@city.nl', '2019-04-30'),
('SE-NGO-1111', 'Nordic Nature', 'Conservation of nordic wildlife.', 'Stockholm Gamla Stan', 'Sweden', '+4681234567', 'nature@nordic.se', '2010-08-15'),
('NO-NGO-1212', 'Fjord Guardians', 'Marine life protection.', 'Oslo Harbor', 'Norway', '+4722123456', 'guardians@fjord.no', '2018-05-20'),
('CH-NGO-1313', 'Alpine Rescue', 'Mountain rescue services.', 'Zurich Mountain Rd', 'Switzerland', '+41441234567', 'rescue@alpine.ch', '1980-12-01'),
('PL-NGO-1414', 'Hope Poland', 'Disaster relief.', 'Warsaw Center', 'Poland', '+48221234567', 'hope@poland.pl', '2022-02-24'),
('GR-NGO-1515', 'History Keepers', 'Archaeological volunteering.', 'Athens Acropolis Way', 'Greece', '+302101234567', 'history@greece.gr', '1995-07-07'),
('PT-NGO-1616', 'Ocean Blue', 'Surf therapy for kids.', 'Lisbon Coast', 'Portugal', '+351211234567', 'surf@ocean.pt', NULL), -- FOUNDING_DATE NULL
('IE-CHARITY-1717', 'Green Isle', 'Reforestation in Ireland.', 'Dublin Park', 'Ireland', '+35311234567', 'trees@green.ie', '2020-03-17'),
('NZ-NGO-1818', 'Kiwi Saver', 'Bird conservation.', 'Wellington Forest', 'New Zealand', '+6441234567', 'kiwi@saver.nz', '2008-09-09'),
('SG-CHARITY-1919', 'Urban Farm', 'Community gardening.', 'Singapore Garden Bay', 'Singapore', '+6561234567', 'farm@urban.sg', '2021-01-01'),
('KR-NGO-2020', 'Tech Mentors', 'Teaching coding to seniors.', 'Seoul Gangnam', 'South Korea', '+82212345678', 'tech@mentors.kr', '2017-11-11');


-- 3. Insert VOLUNTEERS (Uses User IDs 3-27)
INSERT INTO volunteers (ID_user, birth_date, skills, availability, emergency_contact)
VALUES
-- Originals (IDs 3-7)
(3, '1995-05-15', 'Teaching, English, Math', 'Weekends', 'Mother: +4477009002'),
(4, '1998-11-20', 'Medical Aid, First Aid, French', 'Mon-Fri Afternoons', 'Husband: +3312345679'),
(5, '2001-03-10', 'Driver, Logistics, German', 'Full-time Summer', 'Father: +4915123457'),
(6, '1990-07-25', 'Coding, Web Design, Japanese', 'Remote only', 'Sister: +8190123457'),
(7, '1999-12-05', 'Cooking, Event Planning, Italian', 'Flexible', 'Brother: +3933312346'),

-- New Volunteers (IDs 8-27 in the volunteer sequence, but referencing created users 13-32)
(13, '2000-01-01', 'Gardening, Painting', 'Weekends', NULL), -- EMERGENCY_CONTACT NULL
(14, '1992-02-14', 'Construction, Carpentry', 'Morning', 'Wife: +111222333'),
(15, '1988-08-08', 'Accounting, Finance', 'Evenings', 'Husband: +444555666'),
(16, '1996-04-20', 'Photography, Social Media', 'Flexible', NULL), -- EMERGENCY_CONTACT NULL
(17, '1994-12-25', 'Writing, Translation', 'Remote', 'Mom: +999888777'),
(18, '1991-06-15', 'Cooking, Nutrition', 'Weekends', 'Dad: +123123123'),
(19, '1985-10-30', 'Engineering, Repair', 'Mon-Wed', 'Son: +321321321'),
(20, '1999-07-31', 'Magic, Entertainment', 'Events', 'Uncle: +555666777'),
(21, '1997-03-17', 'Running, Coaching', 'Mornings', NULL), -- EMERGENCY_CONTACT NULL
(22, '1989-11-11', 'Sailing, Navigation', 'Summer', 'Brother: +777888999'),
(23, '1993-09-09', 'Legal advice, Research', 'Flexible', 'Sister: +000111222'),
(24, '1995-05-25', 'Archeology, History', 'Full-time', 'Aunt: +333222111'),
(25, '1987-02-28', 'Plumbing, Mechanics', 'On call', 'Cousin: +666555444'),
(26, '1990-01-20', 'Hiking, Survival skills', 'Weekends', 'Friend: +999000111'),
(27, '1998-12-12', 'Advocacy, Public Speaking', 'Evenings', 'Mom: +111000222'),
(28, '1994-04-04', 'Science, Chemistry', 'Weekdays', 'Lab Partner: +555444333'),
(29, '1992-06-06', 'Psychology, Counseling', 'Afternoons', 'Dad: +222333444'),
(30, '1991-08-20', 'Fashion, Sewing', 'Flexible', NULL), -- EMERGENCY_CONTACT NULL
(31, '1920-07-04', 'Leadership, Strategy', 'Anytime', 'Bucky: +19451945'),
(32, '1970-05-29', 'Engineering, Robotics', 'Busy', 'Pepper: +1000000');


-- 4. Insert COORDINATORS (Uses User IDs 8-12 + 33-55)
-- Linking new coordinators to new ONGs (roughly 1-to-1)
INSERT INTO coordinators (ID_user, ong_registration_number, department, experience_years, employment_type)
VALUES
-- Originals (IDs 8-12)
(8, 'UK-REG-2020-001', 'Environmental Projects', 5, 'Full-time'),
(9, 'RO-NGO-556677', 'Educational Programs', 8, 'Full-time'),
(10, 'US-501C3-9988', 'IT Support & Training', 3, 'Part-time'),
(11, 'FR-ASSO-1122', 'Field Operations', 10, 'Contract'),
(12, 'AU-ABN-778899', 'Community Outreach', 4, 'Full-time'),

-- New Coordinators (IDs 33-55) linked to new ONGs
(33, 'DE-NGO-1001', 'Child Welfare', 6, 'Full-time'),
(34, 'IT-ONLUS-2002', 'Events', 2, 'Part-time'),
(35, 'ES-ONG-3003', 'Veterinary', 9, 'Full-time'),
(36, 'JP-NPO-4004', 'Home Care', 15, 'Full-time'),
(37, 'CA-CHARITY-5005', 'Shelter Mgmt', 4, 'Contract'),
(38, 'BR-NGO-6006', 'Conservation', 7, 'Full-time'),
(39, 'IN-NGO-7007', 'Sanitation', 3, 'Volunteer-basis'),
(40, 'ZA-NPO-8008', 'Education', 5, 'Part-time'),
(41, 'MX-NGO-9009', 'Logistics', 8, 'Full-time'),
(42, 'NL-ANBI-1010', 'Urban Planning', 4, 'Part-time'),
(43, 'SE-NGO-1111', 'Research', 6, 'Contract'),
(44, 'NO-NGO-1212', 'Marine Bio', 10, 'Full-time'),
(45, 'CH-NGO-1313', 'Emergency', 12, 'Full-time'),
(46, 'PL-NGO-1414', 'Crisis Mgmt', 3, 'Volunteer-basis'),
(47, 'GR-NGO-1515', 'Excavation', 20, 'Contract'),
(48, 'PT-NGO-1616', 'Therapy', 5, 'Part-time'),
(49, 'IE-CHARITY-1717', 'Forestry', 2, 'Full-time'),
(50, 'NZ-NGO-1818', 'Zoology', 7, 'Full-time'),
(51, 'SG-CHARITY-1919', 'Horticulture', 4, 'Part-time'),
(52, 'KR-NGO-2020', 'IT Education', 6, 'Full-time'),
(53, 'UK-REG-2020-001', 'Fundraising', 2, 'Intern'),
(54, 'US-501C3-9988', 'HR', 10, 'Full-time'),
(55, 'RO-NGO-556677', 'Marketing', 3, 'Part-time');


-- 5. Insert ACTIVITY CATEGORIES (Total: 25 categories)
INSERT INTO activity_categories (name, description)
VALUES
-- Originals
('Environment', 'Tree planting, cleanup'),
('Education', 'Tutoring, mentoring'),
('Healthcare', 'Medical camps'),
('Technology', 'Coding bootcamps'),
('Social Welfare', 'Food banks'),

-- New entries (20)
('Animal Rights', 'Shelter help, adoption events'),
('Arts & Culture', 'Museum guides, painting workshops'),
('Sports', 'Coaching, event organization'),
('Disaster Relief', 'Immediate response to crises'),
('Legal Aid', 'Pro bono legal services'),
('Elderly Care', 'Companionship for seniors'),
('Youth Development', 'Leadership camps'),
('Housing', 'Building homes, repairs'),
('Mental Health', 'Counseling, support groups'),
('Human Rights', 'Advocacy and awareness'),
('Marine Life', 'Beach cleanups, diving'),
('Agriculture', 'Farming support'),
('Heritage', 'Restoration of monuments'),
('Science', 'Citizen science projects'),
('Disability Support', 'Accessibility assistance'),
('Refugee Aid', 'Integration support'),
('Literacy', 'Reading programs'),
('Music', 'Charity concerts'),
('Community Dev', 'Neighborhood improvement'),
('Wildlife', 'Tracking and protection');


-- 6. Insert ACTIVITIES (Total: 28 Activities)
-- IDs will be auto-generated (1-28)
-- NOTE: status 'open', 'closed', 'completed', 'planning'
INSERT INTO activities (ID_category, ID_coordinator, name, description, location, start_date, end_date, max_volunteers, status, donations_collected)
VALUES
-- Originals (Coordinators 1-5)
(1, 1, 'London Park Re-wilding', 'Planting native species.', 'Hyde Park, London', '2026-03-15 09:00:00', '2026-03-15 16:00:00', 50, 'open', 1500.00),
(4, 3, 'Coding for Kids Workshop', 'Intro to Python.', 'Online via Zoom', '2026-04-01 14:00:00', '2026-04-01 18:00:00', 10, 'open', 300.50),
(3, 4, 'Rural Health Checkup', 'Mobile clinic.', 'Village Square, Lyon', '2026-05-10 08:00:00', '2026-05-12 18:00:00', 20, 'planning', 5000.00),
(2, 2, 'Bucharest Homework Club', 'Math homework help.', 'School No. 1, Bucharest', '2026-02-20 15:00:00', '2026-06-20 17:00:00', 5, 'open', 100.00),
(1, 5, 'Bondi Beach Cleanup', 'Removing microplastics.', 'Bondi Beach, Sydney', '2026-01-20 07:00:00', '2026-01-20 12:00:00', 100, 'completed', 0.00),

-- New Activities (Linked to new coordinators 6-28)
(6, 6, 'Berlin Animal Adoption', 'Help finding homes for pets.', 'Berlin Central Park', '2026-06-01 10:00:00', '2026-06-01 18:00:00', 15, 'open', 200.00),
(7, 7, 'Rome Art Festival', 'Guide for tourists.', 'Colosseum Area', '2026-07-10 09:00:00', '2026-07-12 20:00:00', 30, 'planning', 1000.00),
(6, 8, 'Madrid Dog Walk', 'Walking shelter dogs.', 'Retiro Park', '2026-04-05 08:00:00', '2026-04-05 12:00:00', 20, 'open', 50.00),
(11, 9, 'Tokyo Elderly Visit', 'Tea time with seniors.', 'Shibuya Community Center', '2026-03-20 14:00:00', '2026-03-20 16:00:00', 5, 'closed', 0.00),
(5, 10, 'Toronto Winter Shelter', 'Serving soup.', 'Downtown Shelter', '2026-01-15 18:00:00', '2026-01-15 22:00:00', 10, 'completed', 500.00),
(1, 11, 'Amazon Reforestation', 'Planting trees.', 'Manaus Reserve', '2026-09-01 08:00:00', '2026-09-10 17:00:00', 100, 'planning', 10000.00),
(3, 12, 'Mumbai Clean Water', 'Distributing filters.', 'Dharavi', '2026-05-05 09:00:00', '2026-05-05 15:00:00', 25, 'open', 1200.00),
(2, 13, 'Cape Town Math Tutoring', 'High school math help.', 'Public Library', '2026-02-28 15:00:00', '2026-02-28 17:00:00', 8, 'open', 0.00),
(5, 14, 'Mexico City Food Drive', 'Packing food boxes.', 'Central Warehouse', '2026-04-10 09:00:00', '2026-04-10 13:00:00', 50, 'open', 3000.00),
(19, 15, 'Amsterdam Bike Repair', 'Fixing bikes for kids.', 'Vondelpark', '2026-05-20 10:00:00', '2026-05-20 14:00:00', 5, 'open', 150.00),
(20, 16, 'Stockholm Wolf Tracking', 'Counting population.', 'North Forests', '2026-10-01 06:00:00', '2026-10-05 18:00:00', 5, 'planning', 2000.00),
(16, 17, 'Oslo Fjord Clean', 'Diving for trash.', 'Oslo Bay', '2026-08-15 10:00:00', '2026-08-15 14:00:00', 12, 'open', 500.00),
(9, 18, 'Zurich Rescue Training', 'Avalanche drill.', 'Alps Base Camp', '2026-12-10 08:00:00', '2026-12-12 16:00:00', 20, 'planning', 0.00),
(9, 19, 'Warsaw Relief', 'Packing supplies.', 'City Hall', '2026-03-01 09:00:00', '2026-03-01 17:00:00', 100, 'completed', 5000.00),
(18, 20, 'Athens Restoration', 'Cleaning marble statues.', 'Museum', '2026-06-15 09:00:00', '2026-06-20 14:00:00', 10, 'open', 800.00),
(16, 21, 'Lisbon Surf Day', 'Teaching kids to surf.', 'Cascais Beach', '2026-07-20 10:00:00', '2026-07-20 16:00:00', 15, 'open', 200.00),
(1, 22, 'Dublin Tree Planting', 'Urban greening.', 'Phoenix Park', '2026-03-17 10:00:00', '2026-03-17 14:00:00', 40, 'open', 1000.00),
(20, 23, 'Kiwi Bird Count', 'Night observation.', 'Zealandia', '2026-04-20 20:00:00', '2026-04-21 02:00:00', 8, 'open', 400.00),
(17, 24, 'Singapore Garden City', 'Planting flowers.', 'Botanic Gardens', '2026-02-14 09:00:00', '2026-02-14 11:00:00', 30, 'closed', 100.00),
(4, 25, 'Seoul AI Bootcamp', 'Basic AI for students.', 'Tech Hub', '2026-08-01 09:00:00', '2026-08-05 17:00:00', 20, 'planning', 1500.00);


-- 7. Insert VOLUNTEER ACTIVITIES (Link Volunteers 1-25 to Activities 1-25)
-- CRITICAL FIX: Replaced NULL feedback with '' or text.
INSERT INTO volunteer_activities (ID_volunteer, ID_activity, enrollment_date, status, hours_completed, feedback)
VALUES
-- Originals
(1, 1, '2026-03-01', 'accepted', 0, 'Looking forward to it'),
(1, 2, '2026-03-10', 'pending', 0, ''),
(2, 3, '2026-04-05', 'accepted', 0, ''),
(3, 1, '2026-03-02', 'rejected', 0, 'Unavailable'),
(4, 2, '2026-03-12', 'accepted', 4, 'Great event!'),
(5, 5, '2026-01-10', 'completed', 5, 'Very hot day'),

-- New entries (20+)
(6, 6, '2026-05-20', 'accepted', 0, ''),
(7, 7, '2026-06-01', 'pending', 0, ''),
(8, 8, '2026-03-30', 'accepted', 0, 'I love dogs'),
(9, 9, '2026-02-15', 'rejected', 0, 'Full capacity'),
(10, 10, '2026-01-01', 'completed', 4, 'Heartwarming'),
(11, 11, '2026-08-01', 'pending', 0, 'Always wanted to go to Amazon'),
(12, 12, '2026-04-20', 'accepted', 0, ''),
(13, 13, '2026-02-10', 'accepted', 0, 'Math is fun'),
(14, 14, '2026-04-01', 'pending', 0, ''),
(15, 15, '2026-05-15', 'accepted', 0, 'I can fix flat tires'),
(16, 16, '2026-09-01', 'pending', 0, ''),
(17, 17, '2026-08-01', 'accepted', 0, 'Have diving license'),
(18, 18, '2026-11-01', 'planning', 0, ''),
(19, 19, '2026-02-28', 'completed', 8, 'Exhausting but worth it'),
(20, 20, '2026-06-10', 'accepted', 0, ''),
(21, 21, '2026-07-01', 'accepted', 0, 'Surf up'),
(22, 22, '2026-03-01', 'accepted', 0, 'Green Ireland'),
(23, 23, '2026-04-10', 'pending', 0, ''),
(24, 24, '2026-02-01', 'rejected', 0, 'Event closed'),
(25, 25, '2026-07-15', 'pending', 0, 'I know Python'),
(1, 6, '2026-05-25', 'accepted', 0, 'Travel plans aligned'),
(2, 7, '2026-07-01', 'pending', 0, ''),
(5, 11, '2026-08-20', 'pending', 0, ''),
(3, 19, '2026-02-28', 'completed', 8, 'Good team'),
(4, 14, '2026-04-05', 'accepted', 0, '');


-- 8. Insert DONATIONS (Total: 25 Donations)
-- Ensure 'notes' is NOT NULL
INSERT INTO donations (ong_registration_number, donor_name, amount, donation_date, type, notes)
VALUES
-- Originals
('UK-REG-2020-001', 'EcoCorp Inc.', 5000.00, '2025-12-15', 'Corporate', 'Annual sponsorship'),
('US-501C3-9988', 'Anonymous', 50.00, '2026-01-05', 'Individual', 'Online donation'),
('FR-ASSO-1122', 'Global Health Fund', 12000.00, '2025-11-20', 'Grant', 'For equipment'),
('RO-NGO-556677', 'Local Bakery', 200.00, '2026-02-01', 'In-Kind', 'Food'),
('AU-ABN-778899', 'Surfers United', 750.00, '2026-01-18', 'Event', 'Fundraiser'),

-- New entries (20)
('DE-NGO-1001', 'Hans Foundation', 2000.00, '2026-01-10', 'Grant', 'General support'),
('IT-ONLUS-2002', 'Museum Lovers', 500.00, '2026-02-15', 'Individual', 'Ticket sales donation'),
('ES-ONG-3003', 'PetShop Madrid', 1000.00, '2026-03-01', 'Corporate', 'Pet food supplies'),
('JP-NPO-4004', 'Tokyo Corp', 10000.00, '2025-12-31', 'Corporate', 'End of year gift'),
('CA-CHARITY-5005', 'Winter Gear Ltd', 3000.00, '2026-01-01', 'In-Kind', 'Coats and boots'),
('BR-NGO-6006', 'Rainforest Alliance', 15000.00, '2025-10-10', 'Grant', 'Project Alpha'),
('IN-NGO-7007', 'Bollywood Star', 5000.00, '2026-02-20', 'Individual', 'Anonymous'),
('ZA-NPO-8008', 'Local Gov', 2500.00, '2026-04-01', 'Grant', 'Youth program'),
('MX-NGO-9009', 'Taco Chain', 400.00, '2026-01-20', 'In-Kind', 'Food for event'),
('NL-ANBI-1010', 'Cycle Shop', 150.00, '2026-03-15', 'Corporate', 'Repair kits'),
('SE-NGO-1111', 'IKEA Foundation', 8000.00, '2026-01-05', 'Grant', 'Conservation fund'),
('NO-NGO-1212', 'Fish Market', 300.00, '2026-02-10', 'Corporate', 'Event sponsorship'),
('CH-NGO-1313', 'Bank of Zurich', 20000.00, '2025-12-25', 'Corporate', 'Holiday donation'),
('PL-NGO-1414', 'Community Chest', 100.00, '2026-03-05', 'Individual', 'Cash'),
('GR-NGO-1515', 'History Uni', 1500.00, '2026-01-30', 'Grant', 'Research materials'),
('PT-NGO-1616', 'Surf School', 200.00, '2026-04-20', 'In-Kind', 'Boards'),
('IE-CHARITY-1717', 'Guinness Store', 1000.00, '2026-03-17', 'Corporate', 'St Patricks Day'),
('NZ-NGO-1818', 'Nature Trust', 5000.00, '2026-02-12', 'Grant', 'Kiwi protection'),
('SG-CHARITY-1919', 'Garden City Fund', 3000.00, '2026-01-25', 'Grant', 'Urban greening'),
('KR-NGO-2020', 'Samsung Electronics', 12000.00, '2026-02-05', 'Corporate', 'Laptops for students');