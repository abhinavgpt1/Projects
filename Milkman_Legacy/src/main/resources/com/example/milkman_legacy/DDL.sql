CREATE DATABASE IF NOT EXISTS milkmandb_legacy;
USE milkmandb_legacy;

CREATE TABLE idpwd (
    id                      VARCHAR(36)     PRIMARY KEY,
    password                VARCHAR(255)    NOT NULL
);

CREATE TABLE customerentry (
    name VARCHAR(255) NOT NULL,
    mobile VARCHAR(20),
    address TEXT,
    cq DECIMAL(10,2),
    cp DECIMAL(10,2),
    bq DECIMAL(10,2),
    bp DECIMAL(10,2),
    dos DATE,
    img_path VARCHAR(500)
);

CREATE TABLE billpanel (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dos DATE NOT NULL,
    doe DATE NOT NULL,
    bill_amount FLOAT,
    cqty FLOAT,
    bqty FLOAT,
    is_paid BOOLEAN
);

CREATE TABLE variationconsole (
    name VARCHAR(255) NOT NULL,
    variation_date DATE NOT NULL,
    cq_variation DECIMAL(10,2),
    bq_variation DECIMAL(10,2)
);