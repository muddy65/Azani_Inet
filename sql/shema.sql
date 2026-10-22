-- Create the database
DROP DATABASE IF EXISTS azani_isp;
CREATE DATABASE azani_isp;
USE azani_isp;

-- 1. Delete the old mismatched tables
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS subscriptions;
DROP TABLE IF EXISTS infrastructure;
DROP TABLE IF EXISTS institutions;


CREATE TABLE institutions (
    institution_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    contact_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    contact_email VARCHAR(100) NOT NULL UNIQUE,
    registration_date DATE NOT NULL
);

CREATE TABLE subscriptions (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_id INT NOT NULL,
    bandwidth_mbps INT NOT NULL,
    monthly_cost DECIMAL(10, 2) NOT NULL,
    connection_status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (institution_id) REFERENCES institutions(institution_id) ON DELETE CASCADE
);

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_id INT NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (institution_id) REFERENCES institutions(institution_id) ON DELETE CASCADE
);