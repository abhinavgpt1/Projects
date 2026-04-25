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
    sname VARCHAR(255) NOT NULL,
    dos DATE NOT NULL,
    doe DATE NOT NULL,
    amount FLOAT,
    cqty FLOAT,
    bqty FLOAT,
    status BOOLEAN DEFAULT FALSE
);

CREATE TABLE variationconsole (
    sname VARCHAR(255) NOT NULL, -- this is done for multiple offsets if incorrect entry is entered from UI for a particular day
    cdate DATE NOT NULL,
    cq DECIMAL(10,2),
    bq DECIMAL(10,2)
);