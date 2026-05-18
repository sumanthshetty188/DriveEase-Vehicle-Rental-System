CREATE DATABASE driveease_db;
USE driveease_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    security_question VARCHAR(200),
    security_answer VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles Table
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_name VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    vehicle_type ENUM('Car','Bike','SUV','Van','Truck') NOT NULL,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    price_per_day DECIMAL(10,2) NOT NULL,
    availability_status ENUM('Available','Rented','Maintenance') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    driving_license VARCHAR(50) NOT NULL UNIQUE,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rentals Table
CREATE TABLE IF NOT EXISTS rentals (
    rental_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    actual_return_date DATE,
    total_amount DECIMAL(10,2) NOT NULL,
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('Active','Returned','Cancelled') DEFAULT 'Active',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    rental_id INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('Cash','Card','Online') DEFAULT 'Cash',
    FOREIGN KEY (rental_id) REFERENCES rentals(rental_id)
);

-- Sample Data

-- Admin user: admin / Admin@123 (SHA-256 hashed)
INSERT INTO users (username, password_hash, role, full_name, email, security_question, security_answer) VALUES
('admin', '3a5b0b01e7b4c1f2d8a9c3e6f0b2d4a7c9e1f3b5d7a0c2e4f6b8d0a2c4e6f8b0', 'ADMIN', 'System Administrator', 'admin@driveease.com', 'What is your pet name?', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'),
('sumanth', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'USER', 'Sumanth Shetty', 'sumanth@gmail.com', 'What is your pet name?', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8');

-- Vehicles
INSERT INTO vehicles (vehicle_name, brand, vehicle_type, registration_number, price_per_day, availability_status) VALUES
('City Drive', 'Honda', 'Car', 'MH-01-AB-1234', 1500.00, 'Available'),
('Swift Dzire', 'Maruti Suzuki', 'Car', 'MH-02-CD-5678', 1200.00, 'Available'),
('Activa 6G', 'Honda', 'Bike', 'MH-03-EF-9012', 300.00, 'Available'),
('Fortuner', 'Toyota', 'SUV', 'MH-04-GH-3456', 4000.00, 'Available'),
('Innova Crysta', 'Toyota', 'Van', 'MH-05-IJ-7890', 2500.00, 'Rented'),
('Thar', 'Mahindra', 'SUV', 'MH-06-KL-2345', 3500.00, 'Available'),
('Duke 390', 'KTM', 'Bike', 'MH-07-MN-6789', 800.00, 'Maintenance');

-- Customers
INSERT INTO customers (full_name, phone, email, driving_license, address) VALUES
('Rahul Sharma', '9876543210', 'rahul@email.com', 'DL-01-20190001234', 'Mumbai, Maharashtra'),
('Priya Patel', '8765432109', 'priya@email.com', 'DL-02-20180005678', 'Pune, Maharashtra'),
('Amit Verma', '7654321098', 'amit@email.com', 'DL-03-20200009012', 'Nashik, Maharashtra'),
('Sneha Joshi', '6543210987', 'sneha@email.com', 'DL-04-20170003456', 'Nagpur, Maharashtra');

-- Rentals (sample active and returned)
INSERT INTO rentals (customer_id, vehicle_id, start_date, end_date, actual_return_date, total_amount, fine_amount, status, created_by) VALUES
(1, 5, '2026-05-10', '2026-05-15', '2026-05-15', 12500.00, 0.00, 'Returned', 1),
(2, 1, '2026-05-16', '2026-05-20', NULL, 6000.00, 0.00, 'Active', 1),
(3, 2, '2026-05-01', '2026-05-05', '2026-05-07', 4800.00, 2400.00, 'Returned', 1);

-- Payments
INSERT INTO payments (rental_id, amount_paid, payment_method) VALUES
(1, 12500.00, 'Cash'),
(2, 6000.00, 'Card'),
(3, 7200.00, 'Online');
