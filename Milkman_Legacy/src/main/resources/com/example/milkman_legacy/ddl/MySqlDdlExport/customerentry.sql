-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 26, 2026 at 01:51 AM
-- Server version: 10.4.13-MariaDB
-- PHP Version: 7.4.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `milkmandb_legacy`
--

-- --------------------------------------------------------

--
-- Table structure for table `customerentry`
--

CREATE TABLE `customerentry` (
  `sname` varchar(255) NOT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `cq` decimal(10,2) DEFAULT NULL,
  `cprice` decimal(10,2) DEFAULT NULL,
  `bq` decimal(10,2) DEFAULT NULL,
  `bprice` decimal(10,2) DEFAULT NULL,
  `dos` date NOT NULL,
  `imgpath` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `customerentry`
--

INSERT INTO `customerentry` (`sname`, `mobile`, `address`, `cq`, `cprice`, `bq`, `bprice`, `dos`, `imgpath`) VALUES
('Rohan', '', '', '1.00', '50.00', '0.00', '0.00', '2026-04-01', 'nil');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customerentry`
--
ALTER TABLE `customerentry`
  ADD PRIMARY KEY (`sname`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
