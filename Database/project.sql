-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 24, 2019 at 12:00 PM
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
('330', 'A000002', '1000.00'),
('333', 'A00001', '741.00'),
('33090', 'A00002009', '1000.00'),
('33099', 'A0000209', '1000.00'),
('330978', 'A0008209', '1000.00'),
('333', 'A10000', '810.00'),
('333', 'A100021', '1000.00'),
('33220327', 'A60', '1000.00'),
('34220328', 'A90', '1000.00'),
('545444', 'B42946', '0.00'),
('33220330', 'B70', '1000.00'),
('33220326', 'B80', '1000.00'),
('5454', 'F46835', '0.00');

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
('330', 'ton', 'ochi'),
('33090', 'ton', 'ochieng'),
('330978', 'tonzh', 'ochieng'),
('33099', 'tonz', 'ochieng'),
('33220326', 'sally', 'lango'),
('33220327', 'sally', 'lango'),
('33220330', 'sally', 'lango'),
('333', 'ton', 'ochi'),
('34220328', 'tonny', 'lango'),
('545', 'Tonny', 'Lango'),
('5454', 'Tonny', 'Lango'),
('545444', 'Tonny', 'Lango');

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
  `print` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fingerprints`
--

INSERT INTO `fingerprints` (`id_number`, `print`) VALUES
('545444', '{\"width\":363,\"height\":374,\"minutiae\":[{\"x\":108,\"y\":108,\"direction\":2.601173153319209,\"type\":\"ending\"},{\"x\":140,\"y\":212,\"direction\":2.2058230621852104,\"type\":\"ending\"},{\"x\":155,\"y\":286,\"direction\":4.5634990327751925,\"type\":\"bifurcation\"},{\"x\":192,\"y\":116,\"direction\":3.0419240010986313,\"type\":\"ending\"},{\"x\":206,\"y\":128,\"direction\":0.24497866312686414,\"type\":\"ending\"},{\"x\":192,\"y\":244,\"direction\":1.1902899496825317,\"type\":\"ending\"},{\"x\":175,\"y\":169,\"direction\":2.9441970937399127,\"type\":\"bifurcation\"},{\"x\":81,\"y\":216,\"direction\":5.275705241876658,\"type\":\"ending\"},{\"x\":148,\"y\":178,\"direction\":5.9917285127017195,\"type\":\"ending\"},{\"x\":228,\"y\":74,\"direction\":3.3865713167166573,\"type\":\"ending\"},{\"x\":267,\"y\":132,\"direction\":3.682012153860377,\"type\":\"bifurcation\"},{\"x\":160,\"y\":240,\"direction\":1.7681918866447774,\"type\":\"ending\"},{\"x\":182,\"y\":188,\"direction\":2.896613990462929,\"type\":\"ending\"},{\"x\":226,\"y\":140,\"direction\":3.43304944806766,\"type\":\"ending\"},{\"x\":234,\"y\":172,\"direction\":3.5644465797227336,\"type\":\"bifurcation\"},{\"x\":238,\"y\":156,\"direction\":0.5028432109278609,\"type\":\"ending\"},{\"x\":254,\"y\":156,\"direction\":3.522099030702158,\"type\":\"ending\"},{\"x\":149,\"y\":145,\"direction\":6.038206644052722,\"type\":\"ending\"},{\"x\":114,\"y\":234,\"direction\":1.8622531212727638,\"type\":\"bifurcation\"},{\"x\":116,\"y\":278,\"direction\":1.5208379310729538,\"type\":\"bifurcation\"},{\"x\":168,\"y\":318,\"direction\":0.8139618212362083,\"type\":\"bifurcation\"},{\"x\":179,\"y\":243,\"direction\":1.5707963267948966,\"type\":\"ending\"}]}');

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
('2019-06-03 01:17:46', 'hi'),
('2019-06-18 11:31:19', '333 ton ochi added.'),
('2019-06-18 11:31:19', 'A00001 created for 333'),
('2019-06-18 11:35:24', '330 ton ochi added.'),
('2019-06-18 11:35:24', 'A000002 created for 330'),
('2019-07-07 22:34:08', 'A10000 created for 333'),
('2019-07-07 22:38:39', 'A100021 created for 333'),
('2019-07-07 22:49:28', '33090 ton ochieng added.'),
('2019-07-07 22:49:28', 'A00002009 created for 33090'),
('2019-07-07 23:07:23', '33099 tonz ochieng added.'),
('2019-07-07 23:07:23', 'A0000209 created for 33099'),
('2019-07-07 23:19:47', '330978 tonzh ochieng added.'),
('2019-07-07 23:19:47', 'A0008209 created for 330978'),
('2019-07-11 11:48:22', NULL),
('2019-07-11 11:49:34', NULL),
('2019-07-11 11:50:13', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 22:59:24', NULL),
('2019-07-14 23:44:17', NULL),
('2019-07-14 23:47:09', NULL),
('2019-07-14 23:47:59', NULL),
('2019-07-14 23:49:01', NULL),
('2019-07-15 00:04:52', NULL),
('2019-07-15 00:05:01', NULL),
('2019-07-15 00:44:00', NULL),
('2019-07-24 01:27:50', '5454 Tonny Lango added.'),
('2019-07-24 01:27:50', 'F46835 created for 5454'),
('2019-07-24 02:46:36', '545 Tonny Lango added.'),
('2019-07-24 02:47:20', '545444 Tonny Lango added.'),
('2019-07-24 02:47:20', 'B42946 created for 545444');

-- --------------------------------------------------------

--
-- Table structure for table `passwords`
--

CREATE TABLE `passwords` (
  `id_number` varchar(100) DEFAULT NULL,
  `password` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `passwords`
--

INSERT INTO `passwords` (`id_number`, `password`) VALUES
('333', '123'),
('330', '123'),
('33090', '123'),
('33099', '123'),
('330978', '123'),
('5454', '123'),
('545444', '123');

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
