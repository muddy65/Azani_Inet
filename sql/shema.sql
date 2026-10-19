-- Create the database
DROP DATABASE IF EXISTS azani_isp;
CREATE DATABASE azani_isp;
USE azani_isp;

-- 1. INSTITUTIONS TABLE
-- Stores the main client details and their contact person
CREATE TABLE institutions (
    institution_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL, -- e.g., Primary, Junior, Senior, College
    contact_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    contact_email VARCHAR(100) NOTT NULL UNIQUE,
    registration_date DATE NOT NULL
);

-- 2. INFRASTRUCTURE TABLE
-- Tracks readiness, PC requirements, and LAN nodes for each institution
CREATE TABLE infrastructure (
    infra_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_id INT NOT NULL,
    is_ready BOOLEAN DEFAULT FALSE,
    number_of_users INT DEFAULT 0,
    pcs_required INT DEFAULT 0,
    lan_nodes_required INT DEFAULT 0,
    FOREIGN KEY (institution_id) REFERENCES institutions(institution_id) ON DELETE CASCADE
);

-- 3. SUBSCRIPTIONS TABLE
-- Tracks the current bandwidth package and connection status
CREATE TABLE subscriptions (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_id INT NOT NULL,
    bandwidth_mbps INT NOT NULL, -- 4, 10, 20, 25, or 50
    monthly_cost DECIMAL(10, 2) NOT NULL,
    connection_status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, OVERDUE, or DISCONNECTED
    FOREIGN KEY (institution_id) REFERENCES institutions(institution_id) ON DELETE CASCADE
);

-- 4. PAYMENTS TABLE
-- A ledger tracking every transaction, fine, and fee paid by the institution
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_id INT NOT NULL,
    payment_type VARCHAR(50) NOT NULL, -- REGISTRATION, INSTALLATION, MONTHLY, FINE, RECONNECTION
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (institution_id) REFERENCES institutions(institution_id) ON DELETE CASCADE
);
