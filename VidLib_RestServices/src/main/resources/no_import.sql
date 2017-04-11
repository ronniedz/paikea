
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
INSERT INTO `user` (`id`, `email`, `email_verified`, `password`, `activated`, `firstname`, `lastname`, `updated_at`, `last_login`, `reset_password_code`, `activated_at`, `created_at`, `activation_code`) VALUES (1, 'ronniedz@gmail.com', 1, '5aLltV9mfyeteSV9ihPo77YtVkLXNpZ25vbi5zeXN0ZW0uZ3NlcnZpY2V', 1, 'Ron', 'Dennison', '2016-09-14 03:19:11', NULL, NULL, NULL, NULL, NULL);
--INSERT INTO `user` (`email`, `email_verified`, `password`, `activated`, `firstname`, `lastname`, `updated_at`, `last_login`, `reset_password_code`, `activated_at`, `created_at`, `activation_code`) VALUES ('123@987654.com', 1, 'hgfdss', 1, 'User2', 'Ulast2', '2016-09-14 03:19:11', NULL, NULL, NULL, NULL, NULL);
--INSERT INTO `user` (`email`, `email_verified`, `password`, `activated`, `firstname`, `lastname`, `updated_at`, `last_login`, `reset_password_code`, `activated_at`, `created_at`, `activation_code`) VALUES ('abc@pqrsw.com', 1, 'opener', 1, 'User3', 'Ulast3', '2016-09-14 03:19:12', NULL, NULL, NULL, NULL, NULL);

COMMIT;

START TRANSACTION;
USE `mydb`;
INSERT INTO `role` (`id`, `name`) VALUES (1, 'user_manager');
INSERT INTO `role` (`id`, `name`) VALUES (2, 'role_editor');
INSERT INTO `role` (`id`, `name`) VALUES (3, 'video_manager');
INSERT INTO `role` (`id`, `name`) VALUES (4, 'playlist_manager');
INSERT INTO `role` (`id`, `name`) VALUES (5, 'ui_manager');
INSERT INTO `role` (`id`, `name`) VALUES (6, 'content_moderator');
INSERT INTO `role` (`id`, `name`) VALUES (7, 'edit_child');
INSERT INTO `role` (`id`, `name`) VALUES (8, 'admin');
INSERT INTO `role` (`id`, `name`) VALUES (9, 'sudo');
INSERT INTO `role` (`id`, `name`) VALUES (10, 'guardian');
INSERT INTO `role` (`id`, `name`) VALUES (11, 'child');
INSERT INTO `role` (`id`, `name`) VALUES (12, 'member');


COMMIT;


START TRANSACTION;
USE `mydb`;
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 2);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 3);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 4);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 5);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 6);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 7);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 8);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 9);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 10);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 12);
COMMIT;

START TRANSACTION;
USE `mydb`;

INSERT INTO `token` (`active`, `audience`, `idp`, `issuer`, `subject`, `user_id`) VALUES (TRUE, 'client.apps.beancabfamilyuser.com', 'accounts.google.com', 'localhost', '113488468913948362093', 1);
COMMIT;



