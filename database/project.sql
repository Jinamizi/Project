-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 18, 2019 at 08:29 AM
-- Server version: 10.1.34-MariaDB
-- PHP Version: 5.6.37

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `project`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_balance` (IN `balance` DECIMAL(10,2))  begin
if balance < 0 then
signal sqlstate '45000'
set message_text = 'check constraints.balance cannot be less than 0';
end if;
end$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_into_logs` (IN `statement` TEXT)  begin
insert into logs(action_time, action) values(now(),statement);
end$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `id_number` varchar(100) NOT NULL,
  `account_number` varchar(40) NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`id_number`, `account_number`, `balance`) VALUES
('33220327', 'A60', '0.00'),
('34220328', 'A90', '0.00'),
('33220330', 'B70', '0.00'),
('33220326', 'B80', '0.00');

--
-- Triggers `accounts`
--
DELIMITER $$
CREATE TRIGGER `accounts_ad` AFTER DELETE ON `accounts` FOR EACH ROW BEGIN
call insert_into_logs(concat("account ", old.account_number," for ",old.id_number," removed"));
end
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `accounts_ai` AFTER INSERT ON `accounts` FOR EACH ROW begin insert into logs (action_time, action) values(now(), concat(new.account_number, " created for ", new.id_number));end
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `accounts_au` AFTER UPDATE ON `accounts` FOR EACH ROW begin
declare statement varchar(100);
set @statement := new.id_number;
if new.account_number <> old.account_number then
set @statement := concat(statement, " updated ", old.account_number," to ",new.account_number);
end if;
if new.balance <> old.balance then
set @statement := concat(statement, " updated ", old.balance," to ",new.balance);
end if;
call insert_into_logs(statement);
end
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `accounts_bi` BEFORE INSERT ON `accounts` FOR EACH ROW call check_balance(new.balance)
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `accounts_bu` BEFORE UPDATE ON `accounts` FOR EACH ROW call check_balance(new.balance)
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `username` varchar(40) NOT NULL,
  `password` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`username`, `password`) VALUES
('Tonny', '1234');

-- --------------------------------------------------------

--
-- Stand-in structure for view `customer_info`
-- (See below for the actual view)
--
CREATE TABLE `customer_info` (
`id_number` varchar(100)
,`first_name` varchar(100)
,`last_name` varchar(100)
,`account_number` varchar(40)
,`balance` decimal(10,2)
);

-- --------------------------------------------------------

--
-- Table structure for table `details`
--

CREATE TABLE `details` (
  `id_number` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL DEFAULT 'N/A',
  `last_name` varchar(100) NOT NULL DEFAULT 'N/A'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `details`
--

INSERT INTO `details` (`id_number`, `first_name`, `last_name`) VALUES
('33220326', 'sally', 'lango'),
('33220327', 'sally', 'lango'),
('33220330', 'sally', 'lango'),
('34220328', 'tonny', 'lango');

--
-- Triggers `details`
--
DELIMITER $$
CREATE TRIGGER `details_ad` AFTER DELETE ON `details` FOR EACH ROW BEGIN
call insert_into_logs(concat(old.id_number,' ',old.first_name,' ',old.last_name,' removed'));
end
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `details_ai` AFTER INSERT ON `details` FOR EACH ROW begin
insert into logs (action_time,action) values(now(),concat(new.id_number, " ", new.first_name, " ", new.last_name," added.")); end
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `details_au` AFTER UPDATE ON `details` FOR EACH ROW begin
declare statement varchar(100);
if new.id_number <> old.id_number then
set @statement := concat(old.id_number, " updated to ", new.id_number);
ELSE
set @statement := concat("account for ", old.id_number);
end if;
if new.first_name <> old.first_name then
set @statement := concat(statement, " updated ", old.first_name," to ",new.first_name);
end if;
if new.last_name <> old.last_name then
set @statement := concat(statement, " updated ", old.last_name," to ",new.last_name);
end if;
call insert_into_logs(statement);
end
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `fingerprints`
--

CREATE TABLE `fingerprints` (
  `id_number` varchar(100) NOT NULL,
  `print` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `logs`
--

CREATE TABLE `logs` (
  `action_time` text,
  `action` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logs`
--

INSERT INTO `logs` (`action_time`, `action`) VALUES
('2019-06-01 15:28:29', '34220326 Tonny Lango added.'),
('2019-06-01 15:58:52', 'A90 created for 34220326'),
('2019-06-01 18:13:11', '34220326 tonny lango added.'),
('2019-06-01 18:13:31', '33220326 sally lango added.'),
('2019-06-01 18:13:44', '33220327 sally lango added.'),
('2019-06-01 18:27:10', '33220330 sally lango added.'),
('2019-06-01 18:27:40', 'B70 created for 33220330'),
('2019-06-03 01:08:52', NULL),
('2019-06-03 01:17:46', 'hi');

-- --------------------------------------------------------

--
-- Table structure for table `passwords`
--

CREATE TABLE `passwords` (
  `id_number` varchar(100) DEFAULT NULL,
  `password` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure for view `customer_info`
--
DROP TABLE IF EXISTS `customer_info`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `customer_info`  AS  select `details`.`id_number` AS `id_number`,`details`.`first_name` AS `first_name`,`details`.`last_name` AS `last_name`,`accounts`.`account_number` AS `account_number`,`accounts`.`balance` AS `balance` from (`details` join `accounts` on((`details`.`id_number` = `accounts`.`id_number`))) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_number`),
  ADD UNIQUE KEY `account_number` (`account_number`),
  ADD KEY `id_number` (`id_number`);

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `details`
--
ALTER TABLE `details`
  ADD PRIMARY KEY (`id_number`);

--
-- Indexes for table `fingerprints`
--
ALTER TABLE `fingerprints`
  ADD UNIQUE KEY `print` (`print`),
  ADD KEY `id_number` (`id_number`);

--
-- Indexes for table `passwords`
--
ALTER TABLE `passwords`
  ADD KEY `password` (`password`),
  ADD KEY `id_number` (`id_number`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accounts`
--
ALTER TABLE `accounts`
  ADD CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`id_number`) REFERENCES `details` (`id_number`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `fingerprints`
--
ALTER TABLE `fingerprints`
  ADD CONSTRAINT `fingerprints_ibfk_1` FOREIGN KEY (`id_number`) REFERENCES `details` (`id_number`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `passwords`
--
ALTER TABLE `passwords`
  ADD CONSTRAINT `passwords_ibfk_1` FOREIGN KEY (`id_number`) REFERENCES `details` (`id_number`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
