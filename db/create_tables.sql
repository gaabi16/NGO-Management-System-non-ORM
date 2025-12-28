-- Users
CREATE TABLE users (
    ID_user SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('volunteer', 'coordinator', 'admin'))
);

-- Volunteers
CREATE TABLE volunteers (
    ID_volunteer SERIAL PRIMARY KEY,
    ID_user INT NOT NULL UNIQUE REFERENCES users(ID_user),
    birth_date DATE,
    skills TEXT,
    availability VARCHAR(100),
    emergency_contact VARCHAR(100)
);

-- ONGs
CREATE TABLE ongs (
    ID_ong SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(200),
    registration_number VARCHAR(50) UNIQUE,
    phone VARCHAR(20),
    email VARCHAR(100),
    founding_date DATE
);

-- Coordinators
CREATE TABLE coordinators (
    ID_coordinator SERIAL PRIMARY KEY,
    ID_user INT NOT NULL UNIQUE REFERENCES users(ID_user),
    ID_ong INT NOT NULL REFERENCES ongs(ID_ong),
    department VARCHAR(100),
    experience_years INT,
    employment_type VARCHAR(50)
);

-- Activity Categories
CREATE TABLE activity_categories (
    ID_category SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Activities
CREATE TABLE activities (
    ID_activity SERIAL PRIMARY KEY,
    ID_ong INT NOT NULL REFERENCES ongs(ID_ong),
    ID_category INT NOT NULL REFERENCES activity_categories(ID_category),
    ID_coordinator INT NOT NULL REFERENCES coordinators(ID_coordinator),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    location VARCHAR(200),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    max_volunteers INT,
    status VARCHAR(50)
);

-- Volunteer Activities (many-to-many)
CREATE TABLE volunteer_activities (
    ID_volunteer INT NOT NULL REFERENCES volunteers(ID_volunteer),
    ID_activity INT NOT NULL REFERENCES activities(ID_activity),
    enrollment_date DATE NOT NULL,
    status VARCHAR(50),
    hours_completed INT,
    feedback TEXT,
    PRIMARY KEY (ID_volunteer, ID_activity)
);

-- Donations
CREATE TABLE donations (
    ID_donation SERIAL PRIMARY KEY,
    ID_ong INT NOT NULL REFERENCES ongs(ID_ong),
    donor_name VARCHAR(100),
    amount DECIMAL(10,2) NOT NULL,
    donation_date DATE NOT NULL,
    type VARCHAR(50),
    notes TEXT
);
