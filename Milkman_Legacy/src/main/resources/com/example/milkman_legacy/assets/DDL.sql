-- =============================================================================
-- MILK SUBSCRIPTION MANAGEMENT - UNIFIED DATABASE SCHEMA
-- =============================================================================
-- Description : Single consolidated database for milk delivery and subscription
-- Database    : milkmandb_legacy
-- Tables      : customer_details, bill_panel, variation_console, idpwd
-- =============================================================================


CREATE DATABASE IF NOT EXISTS milkmandb_legacy;
USE milkmandb_legacy;


-- -----------------------------------------------------------------------------
-- Table 1: customer_details
-- Core customer registry with subscription quantities, pricing, and profile.
-- -----------------------------------------------------------------------------
CREATE TABLE customer_details (
    cust_id                 VARCHAR(36)     PRIMARY KEY,   -- UUID for each customer
    cust_name               VARCHAR(50)     NOT NULL,      -- Full name of the customer
    cust_mobile             VARCHAR(15),                   -- Contact phone number
    cust_address            VARCHAR(200),                  -- Delivery address
    cow_milk_qty            FLOAT,                         -- Daily cow milk quantity (litres)
    cow_milk_price          FLOAT,                         -- Price per litre for cow milk
    buffalo_milk_qty        FLOAT,                         -- Daily buffalo milk quantity (litres)
    buffalo_milk_price      FLOAT,                         -- Price per litre for buffalo milk
    start_date              DATETIME        NOT NULL,      -- Subscription start date
    end_date                DATETIME,                      -- Subscription end date (NULL = active)
    cust_imgpath            VARCHAR(300)                   -- Path to customer profile image
);


-- -----------------------------------------------------------------------------
-- Table 2: bill_panel
-- Billing records per customer for each subscription period.
-- -----------------------------------------------------------------------------
CREATE TABLE bill_panel (
    bill_id                 INT             PRIMARY KEY AUTO_INCREMENT,  -- Auto-incremented bill ID
    cust_id                 VARCHAR(36)     NOT NULL,              -- FK → customer_details.cust_id
    start_date              DATETIME        NOT NULL,              -- Billing period start
    end_date                DATETIME        NOT NULL,              -- Billing period end
    cow_milk_qty            FLOAT,                                 -- Total cow milk billed (litres)
    buffalo_milk_qty        FLOAT,                                 -- Total buffalo milk billed (litres)
    amount                  FLOAT,                                 -- Total bill amount
    status                  VARCHAR(10),                           -- Payment status: 'paid' | 'pending'

    CONSTRAINT fk_bill_customer FOREIGN KEY (cust_id)
        REFERENCES customer_details (cust_id)
);


-- -----------------------------------------------------------------------------
-- Table 3: variation_console
-- Daily log of quantity adjustments/changes for a customer's delivery.
-- -----------------------------------------------------------------------------
CREATE TABLE variation_console (
    variation_id            INT             PRIMARY KEY AUTO_INCREMENT,  -- Auto-incremented log ID
    cust_id                 VARCHAR(36)     NOT NULL,              -- FK → customer_details.cust_id
    variation_date          DATE            NOT NULL,              -- Date of the variation
    cow_milk_qty            FLOAT,                                 -- Adjusted cow milk quantity
    buffalo_milk_qty        FLOAT,                                 -- Adjusted buffalo milk quantity

    CONSTRAINT fk_variation_customer FOREIGN KEY (cust_id)
        REFERENCES customer_details (cust_id)
);


-- -----------------------------------------------------------------------------
-- Table 4: idpwd
-- Login credentials for system users (admin / staff).
-- Note: Passwords should be stored as hashed values in production.
-- -----------------------------------------------------------------------------
CREATE TABLE idpwd (
    id                      VARCHAR(36)     PRIMARY KEY,   -- UUID-based user identifier
    password                VARCHAR(255)    NOT NULL       -- Hashed password
);


-- =============================================================================
-- TABLE SUMMARY
-- =============================================================================
--  #  | Table Name          | Purpose
-- ----|---------------------|---------------------------------------------------
--  1  | customer_details    | Customer profiles, milk quantities & pricing
--  2  | bill_panel          | Billing records per subscription period
--  3  | variation_console   | Daily delivery quantity adjustments
--  4  | idpwd               | System user login credentials
-- =============================================================================
-- END OF SCHEMA
-- =============================================================================