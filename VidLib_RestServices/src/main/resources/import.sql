

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';




DROP SCHEMA IF EXISTS `mydb` ;
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;




DROP TABLE IF EXISTS `age_group` ;

CREATE TABLE IF NOT EXISTS `age_group` (   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,   `start` TINYINT(4) NOT NULL,   `end` TINYINT(4) NULL DEFAULT NULL,   `label` VARCHAR(45) NOT NULL DEFAULT 'age -range- label',   PRIMARY KEY (`id`)) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `title_UNIQUE` ON `age_group` (`label` ASC);





DROP TABLE IF EXISTS `user` ;

CREATE TABLE IF NOT EXISTS `user` (   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,   `email` VARCHAR(254) NOT NULL,   `email_verified` TINYINT(4) NOT NULL DEFAULT '0',   `password` VARCHAR(255) NULL DEFAULT NULL,   `activated` TINYINT(4) NULL DEFAULT '0',   `firstname` VARCHAR(127) NULL DEFAULT NULL,   `lastname` VARCHAR(127) NULL DEFAULT NULL,   `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,   `last_login` TIMESTAMP NULL DEFAULT NULL,   `reset_password_code` VARCHAR(255) NULL DEFAULT NULL,   `activated_at` TIMESTAMP NULL DEFAULT NULL,   `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,   `activation_code` VARCHAR(255) NULL DEFAULT NULL,   PRIMARY KEY (`id`)) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;





DROP TABLE IF EXISTS `child` ;

CREATE TABLE IF NOT EXISTS `child` (   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,   `name` VARCHAR(45) NOT NULL,   `age_group_id` INT(10) UNSIGNED NULL DEFAULT NULL,   `user_id` INT(10) UNSIGNED NOT NULL,   `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   PRIMARY KEY (`id`),   CONSTRAINT `fk_age`     FOREIGN KEY (`age_group_id`)     REFERENCES `age_group` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_guardian`     FOREIGN KEY (`user_id`)     REFERENCES `user` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `name_UNIQUE` ON `child` (`name` ASC);

CREATE INDEX `fk_owner_idx` ON `child` (`user_id` ASC);

CREATE INDEX `fk_age_idx` ON `child` (`age_group_id` ASC);





DROP TABLE IF EXISTS `playlist` ;

CREATE TABLE IF NOT EXISTS `playlist` (   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,   `user_id` INT(10) UNSIGNED NOT NULL,   `title` VARCHAR(45) NOT NULL,   `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   PRIMARY KEY (`id`),   CONSTRAINT `fk_user`     FOREIGN KEY (`user_id`)     REFERENCES `user` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE INDEX `user_idx` ON `playlist` (`user_id` ASC);





DROP TABLE IF EXISTS `child_playlist` ;

CREATE TABLE IF NOT EXISTS `child_playlist` (   `child_id` INT(10) UNSIGNED NOT NULL,   `playlist_id` INT(10) UNSIGNED NOT NULL,   `created` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,   `completed` TINYINT(4) NOT NULL DEFAULT '0',   `percent_comp` TINYINT(4) NULL DEFAULT '0',   PRIMARY KEY (`child_id`, `playlist_id`),   CONSTRAINT `fk_cp_child`     FOREIGN KEY (`child_id`)     REFERENCES `child` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_cp_playlist`     FOREIGN KEY (`playlist_id`)     REFERENCES `playlist` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE INDEX `fk_playlist_id_idx` ON `child_playlist` (`playlist_id` ASC);





DROP TABLE IF EXISTS `video` ;

CREATE TABLE IF NOT EXISTS `video` (   `video_id` VARCHAR(255) NOT NULL,   `title` VARCHAR(255) NOT NULL,   `length` INT(11) NULL DEFAULT NULL,   `description` MEDIUMTEXT NULL DEFAULT NULL,   `default_thumbnail` VARCHAR(255) NOT NULL,   `etag` VARCHAR(255) NOT NULL,   `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   `user_id` INT(10) UNSIGNED NOT NULL,   `lang` VARCHAR(3) NOT NULL DEFAULT 'en',   `published_at` DATETIME NULL DEFAULT NULL,   PRIMARY KEY (`video_id`),   CONSTRAINT `video_submitter_idx`     FOREIGN KEY (`user_id`)     REFERENCES `user` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARACTER SET = utf8;

CREATE INDEX `video_submitter_idx_idx` ON `video` (`user_id` ASC);

CREATE UNIQUE INDEX `video_id_UNIQUE` ON `video` (`video_id` ASC);





-- DROP TABLE IF EXISTS `child_video` ;
-- 
-- CREATE TABLE IF NOT EXISTS `child_video` (   `child_id` INT(10) UNSIGNED NOT NULL,   `video_id` VARCHAR(255) NOT NULL,   `completed` TINYINT(4) NOT NULL DEFAULT '0',   `percent_comp` TINYINT(4) NULL DEFAULT '0',   `created` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,   PRIMARY KEY (`child_id`, `video_id`),   CONSTRAINT `fk_cv_child`     FOREIGN KEY (`child_id`)     REFERENCES `child` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_cv_video`     FOREIGN KEY (`video_id`)     REFERENCES `video` (`video_id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;
-- 
-- CREATE INDEX `fk_video_idx` ON `child_video` (`video_id` ASC);


DROP TABLE IF EXISTS `genre` ;

CREATE TABLE IF NOT EXISTS `genre` (   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,   `name` VARCHAR(45) NOT NULL,   `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   `genre_id` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT 'The Parent if not NULL',   PRIMARY KEY (`id`),   CONSTRAINT `fk_super`     FOREIGN KEY (`genre_id`)     REFERENCES `genre` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE INDEX `fk_super_idx` ON `genre` (`genre_id` ASC);



DROP TABLE IF EXISTS `gsso_user` ;

CREATE TABLE IF NOT EXISTS `gsso_user` (   `sub` INT(11) NOT NULL COMMENT 'always\nAn identifier for the user, unique among all Google accounts and never reused. A Google account can have multiple emails at different points in time, but the sub value is never changed. Use sub within your application as the unique-identifier key for the user.',   `iss` VARCHAR(63) NULL DEFAULT NULL COMMENT 'The Issuer Identifier for the Issuer of the response. Always https://accounts.google.com or accounts.google.com for Google ID tokens.',   `email_verified` TINYINT(4) NULL DEFAULT '0',   `azp` VARCHAR(127) NULL DEFAULT NULL COMMENT 'The client_id of the authorized presenter. This claim is only needed when the party requesting the ID token is not the same as the audience of the ID token. This may be the case at Google for hybrid apps where a web application and Android app have a different client_id but share the same project.',   `email` VARCHAR(254) NULL DEFAULT NULL COMMENT 'The user\'s email address. This may not be unique and is not suitable for use as a primary key. Provided only if your scope included the string \"email\"',   `profile` VARCHAR(255) NULL DEFAULT NULL COMMENT 'The URL of the user\'s profile page. Might be provided when:\n	•	The request scope included the string \"profile\"\n	•	The ID token is returned from a token refresh\nWhen profile claims are present, you can use them to update your app\'s user records. Note that this claim is never guaranteed to be present.',   `picture` VARCHAR(127) NULL DEFAULT NULL COMMENT 'The URL of the user\'s profile picture. Might be provided when:\n	•	The request scope included the string \"profile\"\n	•	The ID token is returned from a token refresh\nWhen picture claims are present, you can use them to update your app\'s user records. Note that this claim is never guaranteed to be present.',   `name` VARCHAR(255) NULL DEFAULT NULL COMMENT 'The user\'s full name, in a displayable form. Might be provided when: \n - The request scope included the string \"profile\"\n - The ID token is returned from a token refresh',   `aud` VARCHAR(63) NOT NULL COMMENT 'always\nIdentifies the audience that this ID token is intended for. It must be one of the OAuth 2.0 client IDs of your application.',   `iat` INT(10) UNSIGNED NOT NULL COMMENT 'The time the ID token was issued, represented in Unix time',   `exp` INT(10) UNSIGNED NOT NULL COMMENT 'always\nThe time the ID token expires, represented in Unix time',   `at_hash` VARCHAR(45) NULL DEFAULT NULL COMMENT 'Access token hash. Provides validation that the access token is tied to the identity token. If the ID token is issued with an access token in the server flow, this is always included. This can be used as an alternate mechanism to protect against cross-site request forgery attacks, but if you follow Step 1 and Step 3 it is not necessary to verify the access token',   `locale` VARCHAR(5) NULL DEFAULT 'en',   `given_name` VARCHAR(127) NULL DEFAULT NULL,   `family_name` VARCHAR(127) NULL DEFAULT NULL,   `app_token` VARCHAR(45) NULL DEFAULT NULL,   `user_id` INT(10) UNSIGNED NOT NULL,   PRIMARY KEY (`user_id`),   CONSTRAINT `fk_gsso_user`     FOREIGN KEY (`user_id`)     REFERENCES `user` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `sub_UNIQUE` ON `gsso_user` (`sub` ASC);

CREATE INDEX `fk_gsso_user_idx` ON `gsso_user` (`user_id` ASC);



DROP TABLE IF EXISTS `playlist_video` ;

CREATE TABLE IF NOT EXISTS `playlist_video` (   `playlist_id` INT(10) UNSIGNED NOT NULL,   `video_id` VARCHAR(255) NOT NULL,   `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   PRIMARY KEY (`playlist_id`, `video_id`),   CONSTRAINT `fk_pv_playlist`     FOREIGN KEY (`playlist_id`)     REFERENCES `playlist` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_pv_video`     FOREIGN KEY (`video_id`)     REFERENCES `video` (`video_id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE INDEX `fk_video_idx` ON `playlist_video` (`video_id` ASC);


DROP TABLE IF EXISTS `video_genre` ;

CREATE TABLE IF NOT EXISTS `video_genre` (   `video_id` VARCHAR(255) NOT NULL,   `genre_id` INT(10) UNSIGNED NOT NULL,   `user_id` INT(10) UNSIGNED NOT NULL,   `genre2_id` INT(10) UNSIGNED NULL DEFAULT NULL,   `created` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,   PRIMARY KEY (`video_id`, `genre_id`),   CONSTRAINT `fk_vg_genre1`     FOREIGN KEY (`genre_id`)     REFERENCES `genre` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_vg_genre2`     FOREIGN KEY (`genre2_id`)     REFERENCES `genre` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_vg_user`     FOREIGN KEY (`user_id`)     REFERENCES `user` (`id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION,   CONSTRAINT `fk_vg_video`     FOREIGN KEY (`video_id`)     REFERENCES `video` (`video_id`)     ON DELETE NO ACTION     ON UPDATE NO ACTION) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;

CREATE INDEX `fk_video_idx` ON `video_genre` (`video_id` ASC);

CREATE INDEX `fk_user_idx` ON `video_genre` (`user_id` ASC);

CREATE INDEX `fk_genre_1_idx` ON `video_genre` (`genre_id` ASC);

CREATE INDEX `fk_genre_2_idx` ON `video_genre` (`genre2_id` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Required menus
START TRANSACTION;
USE `mydb`;
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (1, 3, 5, '3 to 5 years old');
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (2, 4, 6, '4 to 6 years old');
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (3, 5, 7, '5 to 7 years old');
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (4, 6, 8, '6 to 8 years old');
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (5, 7, 9, '7 to 9 years old');
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (6, 8, 10, '8 to 10 years old');
INSERT INTO `age_group` (`id`, `start`, `end`, `label`) VALUES (7, 9, 11, '9 to 11 years old');

COMMIT;

-- Required menus
START TRANSACTION;
USE `mydb`;
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (1, 'Uncategorized', '2016-09-14 03:19:11', \N);
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (2, 'Fantasy', '2016-09-14 03:19:11', \N);
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (3, 'Sing-along', '2016-09-14 03:19:11', \N);
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (4, 'Animation', '2016-09-14 03:19:11', \N);
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (5, '3D', '2016-09-14 03:19:11', 4);
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (6, '4D', '2016-09-14 03:19:12', 4);
INSERT INTO `genre` (`id`, `name`, `created`, `genre_id`) VALUES (7, 'Action', '2016-09-14 03:19:11', \N);

COMMIT;



-- INSERT 3 Users
START TRANSACTION;
USE `mydb`;
INSERT INTO `user` (`id`, `email`, `email_verified`, `password`, `activated`, `firstname`, `lastname`, `updated_at`, `last_login`, `reset_password_code`, `activated_at`, `created_at`, `activation_code`) VALUES (1, 'sun@mercury.com1', 1, 'qwerty', 1, 'User1', 'Ulast1', '2016-09-14 03:19:11', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` (`id`, `email`, `email_verified`, `password`, `activated`, `firstname`, `lastname`, `updated_at`, `last_login`, `reset_password_code`, `activated_at`, `created_at`, `activation_code`) VALUES (2, '123@987654.com', 1, 'hgfdss', 1, 'User2', 'Ulast2', '2016-09-14 03:19:11', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` (`id`, `email`, `email_verified`, `password`, `activated`, `firstname`, `lastname`, `updated_at`, `last_login`, `reset_password_code`, `activated_at`, `created_at`, `activation_code`) VALUES (3, 'abc@pqrsw.com', 1, 'opener', 1, 'User3', 'Ulast3', '2016-09-14 03:19:12', NULL, NULL, NULL, NULL, NULL);

COMMIT;

-- INSERT 1 child
START TRANSACTION;
USE `mydb`;
INSERT INTO `child` (`id`, `name`, `age_group_id`, `user_id`, `created`) VALUES (1, 'Freddie Kruger', 7, 1, '2016-09-14 03:19:11');

COMMIT;




-- INSERT 2 Playlists
START TRANSACTION;
USE `mydb`;
INSERT INTO `playlist` (`id`, `user_id`, `title`, `created`) VALUES (1, 1, 'pltitle', '2016-09-14 03:19:11');
INSERT INTO `playlist` (`id`, `user_id`, `title`, `created`) VALUES (2, 2, 'ptitle2', '2016-09-14 03:19:11');
COMMIT;




-- START TRANSACTION;
-- USE `mydb`;
-- INSERT INTO `video` (`video_id`, `title`, `length`, `description`, `default_thumbnail`, `etag`, `created`, `user_id`, `lang`, `published_at`) VALUES ('googid1', 'vidtitle', 1200, 'qwerty ytrewq qwerty0', '/images/thumb.jpg', 'mnbvcxz', '2016-09-14 03:19:11', 1, 'en', '2016-09-14 03:19:11');
-- INSERT INTO `video` (`video_id`, `title`, `length`, `description`, `default_thumbnail`, `etag`, `created`, `user_id`, `lang`, `published_at`) VALUES ('googid2', 'vidtitle-2', 3000, 'qwerty ytrewq qwerty1', '/images/thumb2.jpg', 'etagged0', '2016-09-14 03:19:11', 1, 'en', '2016-09-14 03:19:11');
-- INSERT INTO `video` (`video_id`, `title`, `length`, `description`, `default_thumbnail`, `etag`, `created`, `user_id`, `lang`, `published_at`) VALUES ('googid3', 'vidtitle-1', 3001, 'qwerty ytrewq qwerty2', '/images/thumb2.jpg', 'etagged1', '2016-09-14 03:19:12', 2, 'en', '2016-09-14 03:19:12');
-- INSERT INTO `video` (`video_id`, `title`, `length`, `description`, `default_thumbnail`, `etag`, `created`, `user_id`, `lang`, `published_at`) VALUES ('googid4', 'vidtitle0', 3002, 'qwerty ytrewq qwerty3', '/images/thumb2.jpg', 'etagged2', '2016-09-14 03:19:13', 3, 'en', '2016-09-14 03:19:13');
-- INSERT INTO `video` (`video_id`, `title`, `length`, `description`, `default_thumbnail`, `etag`, `created`, `user_id`, `lang`, `published_at`) VALUES ('googid5', 'vidtitle1', 3003, 'qwerty ytrewq qwerty4', '/images/thumb2.jpg', 'etagged3', '2016-09-14 03:19:14', 3, 'en', '2016-09-14 03:19:14');
-- 
-- COMMIT;












