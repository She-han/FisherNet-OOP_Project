# FisherNet-OOP_Project
FisherNet (IoT-Enabled Smart Fishing Boat Management System) - OOP Group Project

================================================================================================
External Libraries:

mysql-connector-java-8.x.x.jar

core-3.5.0.jar

javase-3.5.0.jar (from ZXing)
==================================================================================================
This module is part of the FisherNet system and handles:

-> Boat registration with QR code generation

-> QR code scanning from .png

-> Database lookup of boat info

-> Fish stock entry linked to scanned boat
=================================================================================================

Step 1: Register Boat
Run BoatRegistrationForm.java

Enter boat name and reg no

QR code is generated and saved to /qr/

Data saved to MySQL

Step 2: Scan QR
Run QRScannerForm.java

Select the .png QR file

Boat is identified via reg number

Option to enter fish stock will appear

Step 3: Enter Stock
Form is pre-filled with boat info

Add species and weight

Record saved to MySQL fish_stock table

========================================================================================================

Database Setup
Run these SQL scripts in MySQL:

CREATE DATABASE fishernet_db;
USE fishernet_db;

CREATE TABLE boats (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    registration_no VARCHAR(50) UNIQUE,
    qr_code_path VARCHAR(255)
);

CREATE TABLE fish_stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    boat_id INT,
    species VARCHAR(100),
    weight DOUBLE,
    date DATE,
    FOREIGN KEY (boat_id) REFERENCES boats(id)
);
