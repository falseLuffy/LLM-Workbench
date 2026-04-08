-- Create Database
CREATE DATABASE IF NOT EXISTS `LLM_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `LLM_db`;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for token_stats
-- ----------------------------
DROP TABLE IF EXISTS `token_stats`;
CREATE TABLE `token_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_cache_hit` tinyint(1) DEFAULT '0',
  `original_model` varchar(50) DEFAULT NULL,
  `used_model` varchar(50) DEFAULT NULL,
  `prompt_tokens` int(11) DEFAULT '0',
  `completion_tokens` int(11) DEFAULT '0',
  `saved_tokens` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `user` (`username`, `password`, `nickname`, `email`) VALUES ('admin', 'admin', 'Administrator', 'admin@example.com');
