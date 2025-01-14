-- noinspection SqlWithoutWhereForFile

-- Delete all data from all tables
DELETE FROM user_access_tokens;
DELETE FROM user_roles;
DELETE FROM event_participants;
DELETE FROM event;
DELETE FROM users;

-- Reset sequence values
ALTER SEQUENCE user_access_tokens_id_seq RESTART WITH 1;
ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE event_event_id_seq RESTART WITH 1;