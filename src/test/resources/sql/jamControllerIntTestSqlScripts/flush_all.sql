-- First, clean up all dependent tables
DELETE FROM jam_participant;
DELETE FROM jam_tags;
DELETE FROM jam_message;

-- Reset user's current jam to null
UPDATE users SET jam_id = NULL WHERE jam_id IS NOT NULL;

-- Then clean up main tables
DELETE FROM jam;
DELETE FROM tag;
DELETE FROM user_roles;
DELETE FROM users;

-- Reset all sequences
ALTER SEQUENCE jam_jam_id_seq RESTART WITH 1;
ALTER SEQUENCE tag_tag_id_seq RESTART WITH 1;
ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE jam_message_jam_message_id_seq RESTART WITH 1;