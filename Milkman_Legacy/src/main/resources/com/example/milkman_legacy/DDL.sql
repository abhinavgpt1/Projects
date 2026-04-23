CREATE DATABASE IF NOT EXISTS milkmandb_legacy;
USE milkmandb_legacy;

CREATE TABLE idpwd (
    id                      VARCHAR(36)     PRIMARY KEY,
    password                VARCHAR(255)    NOT NULL
);

CREATE TABLE customerentry (
    sname VARCHAR(255) PRIMARY KEY,
    mobile VARCHAR(20),
    address TEXT,
    cq DECIMAL(10,2),
    cprice DECIMAL(10,2),
    bq DECIMAL(10,2),
    bprice DECIMAL(10,2),
    dos DATE NOT NULL, -- date of start i.e. subscription start date
    imgpath VARCHAR(500)
);

CREATE TABLE billpanel (
    sname VARCHAR(255) PRIMARY KEY,
    status BOOLEAN DEFAULT FALSE,
    cqty FLOAT,
    bqty FLOAT,
    amount FLOAT,
    dos DATE NOT NULL,
    doe DATE NOT NULL
);

CREATE TABLE variationconsole (
    sname VARCHAR(255) PRIMARY KEY,
    cdate DATE NOT NULL,
    cq DECIMAL(10,2),
    bq DECIMAL(10,2)
);