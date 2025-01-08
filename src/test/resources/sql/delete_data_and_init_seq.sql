-- noinspection SqlWithoutWhereForFile

-- Delete all data from all tables
DELETE FROM user_access_tokens;
DELETE FROM user_roles;
DELETE FROM users;

-- Reset all sequences
-- ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
