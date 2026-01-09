-- Users (Neschimbat)
CREATE TABLE users (
    ID_user SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('volunteer', 'coordinator', 'admin'))
);

-- Volunteers (emergency_contact este acum NULLABLE)
CREATE TABLE volunteers (
    ID_volunteer SERIAL PRIMARY KEY,
    ID_user INT NOT NULL UNIQUE REFERENCES users(ID_user),
    birth_date DATE NOT NULL,
    skills TEXT NOT NULL,
    availability VARCHAR(100) NOT NULL,
    emergency_contact VARCHAR(100) -- Acum permite NULL
);

-- ONGs (founding_date este acum NULLABLE)
CREATE TABLE ongs (
    registration_number VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(200) NOT NULL,
    country VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    founding_date DATE -- Acum permite NULL
);

-- Coordinators (Toate câmpurile rămân NOT NULL)
CREATE TABLE coordinators (
    ID_coordinator SERIAL PRIMARY KEY,
    ID_user INT NOT NULL UNIQUE REFERENCES users(ID_user),
    ong_registration_number VARCHAR(50) NOT NULL REFERENCES ongs(registration_number),
    department VARCHAR(100) NOT NULL,
    experience_years INT NOT NULL,
    employment_type VARCHAR(50) NOT NULL
);

-- Activity Categories
CREATE TABLE activity_categories (
    ID_category SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Activities (Toate câmpurile rămân NOT NULL)
CREATE TABLE activities (
    ID_activity SERIAL PRIMARY KEY,
    ID_coordinator INT NOT NULL REFERENCES coordinators(ID_coordinator),
    ID_category INT NOT NULL REFERENCES activity_categories(ID_category),
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(200) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    max_volunteers INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    donations_collected DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

-- Volunteer Activities (Toate câmpurile rămân NOT NULL)
CREATE TABLE volunteer_activities (
    ID_volunteer INT NOT NULL REFERENCES volunteers(ID_volunteer),
    ID_activity INT NOT NULL REFERENCES activities(ID_activity),
    enrollment_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    hours_completed INT NOT NULL DEFAULT 0,
    feedback TEXT NOT NULL DEFAULT '',
    PRIMARY KEY (ID_volunteer, ID_activity)
);

-- Donations (Toate câmpurile rămân NOT NULL)
CREATE TABLE donations (
    ID_donation SERIAL PRIMARY KEY,
    ong_registration_number VARCHAR(50) NOT NULL REFERENCES ongs(registration_number),
    donor_name VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    donation_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    notes TEXT NOT NULL
);