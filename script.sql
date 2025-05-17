Create DATABASE ramal_db;

CREATE TABLE `users` (
  `id` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
);

CREATE TABLE `extensions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `extension_number` varchar(36) NOT NULL,
  `logged_user` char(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `extension_number` (`extension_number`),
  KEY `logged_user` (`logged_user`),
  CONSTRAINT `extensions_ibfk_1` FOREIGN KEY (`logged_user`) REFERENCES `users` (`id`)
);

CREATE TABLE `extension_range` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_range` int NOT NULL,
  `end_range` int NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `extensions` (`extension_number`, `logged_user`) VALUES
('2000', NULL),
('2001', NULL),
('2002', NULL),
('2003', NULL),
('2004', NULL),
('2005', NULL),
('2006', NULL),
('2007', NULL),
('2008', NULL),
('2009', NULL),
('2010', NULL),
('2011', NULL),
('2012', NULL),
('2013', NULL),
('2014', NULL),
('2015', NULL),
('2016', NULL),
('2017', NULL),
('2018', NULL),
('2019', NULL),
('2020', NULL),
('2021', NULL),
('2022', NULL),
('2023', NULL),