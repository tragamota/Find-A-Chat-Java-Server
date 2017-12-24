-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server versie:                10.2.8-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Versie:              9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Databasestructuur van location_aware_database wordt geschreven
CREATE DATABASE IF NOT EXISTS `location_aware_database` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `location_aware_database`;

-- Structuur van  tabel location_aware_database.authentication wordt geschreven
CREATE TABLE IF NOT EXISTS `authentication` (
  `Username` varchar(23) NOT NULL,
  `Password` varchar(23) NOT NULL,
  `IdToken` varchar(50) NOT NULL,
  PRIMARY KEY (`Username`),
  KEY `FK_authentication_userinfo` (`IdToken`),
  CONSTRAINT `FK_authentication_userinfo` FOREIGN KEY (`IdToken`) REFERENCES `userinfo` (`IdToken`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporteren was gedeselecteerd
-- Structuur van  tabel location_aware_database.chat wordt geschreven
CREATE TABLE IF NOT EXISTS `chat` (
  `ChatID` varchar(100) NOT NULL,
  `User1` varchar(50) NOT NULL,
  `User2` varchar(50) NOT NULL,
  `ChatStart` datetime NOT NULL,
  PRIMARY KEY (`ChatID`),
  KEY `FK_Chat_userinfo` (`User1`),
  KEY `FK_Chat_userinfo_2` (`User2`),
  CONSTRAINT `FK_Chat_userinfo` FOREIGN KEY (`User1`) REFERENCES `userinfo` (`IdToken`),
  CONSTRAINT `FK_Chat_userinfo_2` FOREIGN KEY (`User2`) REFERENCES `userinfo` (`IdToken`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporteren was gedeselecteerd
-- Structuur van  tabel location_aware_database.chatmessage wordt geschreven
CREATE TABLE IF NOT EXISTS `chatmessage` (
  `ChatID` varchar(100) NOT NULL,
  `To` varchar(50) NOT NULL,
  `From` varchar(50) NOT NULL,
  `Content` text NOT NULL,
  `MessageTime` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `Read` tinyint(1) NOT NULL,
  PRIMARY KEY (`ChatID`,`MessageTime`),
  KEY `FK_ChatMessage_userinfo_2` (`From`),
  KEY `FK_chatmessage_userinfo` (`To`),
  CONSTRAINT `FK_ChatMessage_chat` FOREIGN KEY (`ChatID`) REFERENCES `chat` (`ChatID`),
  CONSTRAINT `FK_ChatMessage_userinfo_2` FOREIGN KEY (`From`) REFERENCES `userinfo` (`IdToken`),
  CONSTRAINT `FK_chatmessage_userinfo` FOREIGN KEY (`To`) REFERENCES `userinfo` (`IdToken`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporteren was gedeselecteerd
-- Structuur van  tabel location_aware_database.location wordt geschreven
CREATE TABLE IF NOT EXISTS `location` (
  `IdToken` varchar(50) NOT NULL,
  `Longitude` double(10,8) NOT NULL,
  `Langitude` double(10,8) NOT NULL,
  PRIMARY KEY (`IdToken`),
  CONSTRAINT `FK__userinfo` FOREIGN KEY (`IdToken`) REFERENCES `userinfo` (`IdToken`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporteren was gedeselecteerd
-- Structuur van  tabel location_aware_database.userinfo wordt geschreven
CREATE TABLE IF NOT EXISTS `userinfo` (
  `IdToken` varchar(50) NOT NULL,
  `NickName` varchar(23) NOT NULL,
  `FirstName` varchar(28) NOT NULL,
  `LastName` varchar(35) NOT NULL,
  `ImagePath` varchar(75) DEFAULT NULL,
  PRIMARY KEY (`IdToken`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporteren was gedeselecteerd
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
