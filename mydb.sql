-- MySQL dump 10.13  Distrib 5.7.15, for osx10.11 (x86_64)
--
-- Host: localhost    Database: mydb
-- ------------------------------------------------------
-- Server version	5.7.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `age_group`
--

DROP TABLE IF EXISTS `age_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `age_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `start` tinyint(4) NOT NULL,
  `end` tinyint(4) DEFAULT NULL,
  `label` varchar(45) NOT NULL DEFAULT 'age -range- label',
  PRIMARY KEY (`id`),
  UNIQUE KEY `title_UNIQUE` (`label`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `age_group`
--

LOCK TABLES `age_group` WRITE;
/*!40000 ALTER TABLE `age_group` DISABLE KEYS */;
INSERT INTO `age_group` VALUES (1,3,5,'3 to 5 years old'),(2,4,6,'4 to 6 years old'),(3,5,7,'5 to 7 years old'),(4,6,8,'6 to 8 years old'),(5,7,9,'7 to 9 years old'),(6,8,10,'8 to 10 years old'),(7,9,11,'9 to 11 years old');
/*!40000 ALTER TABLE `age_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `child`
--

DROP TABLE IF EXISTS `child`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `child` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `age_group_id` int(10) unsigned DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `fk_owner_idx` (`user_id`),
  KEY `fk_age_idx` (`age_group_id`),
  CONSTRAINT `fk_age` FOREIGN KEY (`age_group_id`) REFERENCES `age_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_guardian` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `child`
--

LOCK TABLES `child` WRITE;
/*!40000 ALTER TABLE `child` DISABLE KEYS */;
INSERT INTO `child` VALUES (1,'Freddie Kruger',7,1,'2016-09-14 10:19:11');
/*!40000 ALTER TABLE `child` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `child_playlist`
--

DROP TABLE IF EXISTS `child_playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `child_playlist` (
  `child_id` int(10) unsigned NOT NULL,
  `playlist_id` int(10) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `completed` tinyint(4) NOT NULL DEFAULT '0',
  `percent_comp` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`child_id`,`playlist_id`),
  KEY `fk_playlist_id_idx` (`playlist_id`),
  CONSTRAINT `fk_cp_child` FOREIGN KEY (`child_id`) REFERENCES `child` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_cp_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `child_playlist`
--

LOCK TABLES `child_playlist` WRITE;
/*!40000 ALTER TABLE `child_playlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `child_playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genre`
--

DROP TABLE IF EXISTS `genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `genre` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `genre_id` int(10) unsigned DEFAULT NULL COMMENT 'The Parent if not NULL',
  PRIMARY KEY (`id`),
  KEY `fk_super_idx` (`genre_id`),
  CONSTRAINT `fk_super` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genre`
--

LOCK TABLES `genre` WRITE;
/*!40000 ALTER TABLE `genre` DISABLE KEYS */;
INSERT INTO `genre` VALUES (1,'Uncategorized','2016-09-14 10:19:11',NULL),(2,'Fantasy','2016-09-14 10:19:11',NULL),(3,'Sing-along','2016-09-14 10:19:11',NULL),(4,'Animation','2016-09-14 10:19:11',NULL),(5,'3D','2016-09-14 10:19:11',4),(6,'4D','2016-09-14 10:19:12',4),(7,'Action','2016-09-14 10:19:11',NULL);
/*!40000 ALTER TABLE `genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gsso_user`
--

DROP TABLE IF EXISTS `gsso_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gsso_user` (
  `sub` int(11) NOT NULL COMMENT 'always\nAn identifier for the user, unique among all Google accounts and never reused. A Google account can have multiple emails at different points in time, but the sub value is never changed. Use sub within your application as the unique-identifier key for the user.',
  `iss` varchar(63) DEFAULT NULL COMMENT 'The Issuer Identifier for the Issuer of the response. Always https://accounts.google.com or accounts.google.com for Google ID tokens.',
  `email_verified` tinyint(4) DEFAULT '0',
  `azp` varchar(127) DEFAULT NULL COMMENT 'The client_id of the authorized presenter. This claim is only needed when the party requesting the ID token is not the same as the audience of the ID token. This may be the case at Google for hybrid apps where a web application and Android app have a different client_id but share the same project.',
  `email` varchar(254) DEFAULT NULL COMMENT 'The user''s email address. This may not be unique and is not suitable for use as a primary key. Provided only if your scope included the string "email"',
  `profile` varchar(255) DEFAULT NULL COMMENT 'The URL of the user''s profile page. Might be provided when:\n	•	The request scope included the string "profile"\n	•	The ID token is returned from a token refresh\nWhen profile claims are present, you can use them to update your app''s user records. Note that this claim is never guaranteed to be present.',
  `picture` varchar(127) DEFAULT NULL COMMENT 'The URL of the user''s profile picture. Might be provided when:\n	•	The request scope included the string "profile"\n	•	The ID token is returned from a token refresh\nWhen picture claims are present, you can use them to update your app''s user records. Note that this claim is never guaranteed to be present.',
  `name` varchar(255) DEFAULT NULL COMMENT 'The user''s full name, in a displayable form. Might be provided when: \n - The request scope included the string "profile"\n - The ID token is returned from a token refresh',
  `aud` varchar(63) NOT NULL COMMENT 'always\nIdentifies the audience that this ID token is intended for. It must be one of the OAuth 2.0 client IDs of your application.',
  `iat` int(10) unsigned NOT NULL COMMENT 'The time the ID token was issued, represented in Unix time',
  `exp` int(10) unsigned NOT NULL COMMENT 'always\nThe time the ID token expires, represented in Unix time',
  `at_hash` varchar(45) DEFAULT NULL COMMENT 'Access token hash. Provides validation that the access token is tied to the identity token. If the ID token is issued with an access token in the server flow, this is always included. This can be used as an alternate mechanism to protect against cross-site request forgery attacks, but if you follow Step 1 and Step 3 it is not necessary to verify the access token',
  `locale` varchar(5) DEFAULT 'en',
  `given_name` varchar(127) DEFAULT NULL,
  `family_name` varchar(127) DEFAULT NULL,
  `app_token` varchar(45) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `sub_UNIQUE` (`sub`),
  KEY `fk_gsso_user_idx` (`user_id`),
  CONSTRAINT `fk_gsso_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gsso_user`
--

LOCK TABLES `gsso_user` WRITE;
/*!40000 ALTER TABLE `gsso_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `gsso_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `title` varchar(45) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_idx` (`user_id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (1,1,'pltitle','2016-09-14 10:19:11'),(2,2,'ptitle2','2016-09-14 10:19:11');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_video`
--

DROP TABLE IF EXISTS `playlist_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist_video` (
  `playlist_id` int(10) unsigned NOT NULL,
  `video_id` varchar(255) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`playlist_id`,`video_id`),
  KEY `fk_video_idx` (`video_id`),
  CONSTRAINT `fk_pv_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pv_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`video_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_video`
--

LOCK TABLES `playlist_video` WRITE;
/*!40000 ALTER TABLE `playlist_video` DISABLE KEYS */;
/*!40000 ALTER TABLE `playlist_video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(254) NOT NULL,
  `email_verified` tinyint(4) NOT NULL DEFAULT '0',
  `password` varchar(255) DEFAULT NULL,
  `activated` tinyint(4) DEFAULT '0',
  `firstname` varchar(127) DEFAULT NULL,
  `lastname` varchar(127) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `last_login` timestamp NULL DEFAULT NULL,
  `reset_password_code` varchar(255) DEFAULT NULL,
  `activated_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `activation_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'sun@mercury.com1',1,'qwerty',1,'User1','Ulast1','2016-09-14 10:19:11',NULL,NULL,NULL,NULL,NULL),(2,'123@987654.com',1,'hgfdss',1,'User2','Ulast2','2016-09-14 10:19:11',NULL,NULL,NULL,NULL,NULL),(3,'abc@pqrsw.com',1,'opener',1,'User3','Ulast3','2016-09-14 10:19:12',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video`
--

DROP TABLE IF EXISTS `video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `video` (
  `video_id` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `length` int(11) DEFAULT NULL,
  `description` mediumtext,
  `default_thumbnail` varchar(255) NOT NULL,
  `etag` varchar(255) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int(10) unsigned NOT NULL,
  `lang` varchar(3) NOT NULL DEFAULT 'en',
  `published_at` datetime DEFAULT NULL,
  PRIMARY KEY (`video_id`),
  UNIQUE KEY `video_id_UNIQUE` (`video_id`),
  KEY `video_submitter_idx_idx` (`user_id`),
  CONSTRAINT `video_submitter_idx` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video`
--

LOCK TABLES `video` WRITE;
/*!40000 ALTER TABLE `video` DISABLE KEYS */;
/*!40000 ALTER TABLE `video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_genre`
--

DROP TABLE IF EXISTS `video_genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `video_genre` (
  `video_id` varchar(255) NOT NULL,
  `genre_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `genre2_id` int(10) unsigned DEFAULT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`,`genre_id`),
  KEY `fk_video_idx` (`video_id`),
  KEY `fk_user_idx` (`user_id`),
  KEY `fk_genre_1_idx` (`genre_id`),
  KEY `fk_genre_2_idx` (`genre2_id`),
  CONSTRAINT `fk_vg_genre1` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_vg_genre2` FOREIGN KEY (`genre2_id`) REFERENCES `genre` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_vg_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_vg_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`video_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_genre`
--

LOCK TABLES `video_genre` WRITE;
/*!40000 ALTER TABLE `video_genre` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_genre` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-12-02  3:29:55
