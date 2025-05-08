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