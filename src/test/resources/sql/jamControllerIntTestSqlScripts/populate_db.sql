-- Clean up existing data
DELETE FROM jam_participant;
DELETE FROM jam_tags;
DELETE FROM jam_message;
DELETE FROM jam;
DELETE FROM tag;
DELETE FROM user_roles;
DELETE FROM users;

-- Reset sequences
ALTER SEQUENCE jam_jam_id_seq RESTART WITH 1;
ALTER SEQUENCE tag_tag_id_seq RESTART WITH 1;
ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE jam_message_jam_message_id_seq RESTART WITH 1;

-- Create test users first
INSERT INTO users (user_id, user_email, user_name, user_provider, user_provider_id, user_img_url, user_has_jam_running, user_country)
VALUES
    (1, 'john.doe@test.com', 'John Doe', 'LOCAL', 'local_1', 'https://placeholder.com/john.jpg', false, 'FR'),
    (2, 'jane.smith@test.com', 'Jane Smith', 'GOOGLE', 'google_1', 'https://placeholder.com/jane.jpg', true, 'US'),
    (3, 'bob.wilson@test.com', 'Bob Wilson', 'SPOTIFY', 'spotify_1', 'https://placeholder.com/bob.jpg', false, 'UK');

-- Add user roles
INSERT INTO user_roles (user_id, user_role)
VALUES
    (1, 'USER'),
    (2, 'USER'),
    (3, 'USER');

-- Create tags (using correct table name 'tag')
INSERT INTO tag (tag_id, tag_label)
VALUES
    (1, 'Rock'),
    (2, 'Jazz'),
    (3, 'Pop'),
    (4, 'Classical');

-- Create jams (ensuring the host_id exists in users table)
INSERT INTO jam (jam_id, host_id, jam_name, jam_scheduled_start, jam_status)
VALUES
    (1, 1, 'Johns Rock Party', '2025-01-14 20:00:00', 'RUNNING'),
    (2, 2, 'Jazz Evening', '2025-01-20 19:00:00', 'SCHEDULED'),
    (3, 3, 'Classical Sunday', '2025-01-13 15:00:00', 'STOPPED'),
    (4, 1, 'Pop Night', '2025-01-15 21:00:00', 'CANCELED');

-- Now we can safely add participants
INSERT INTO jam_participant (jam_id, user_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (2, 3),
    (3, 3),
    (3, 1);

-- Associate tags with jams
INSERT INTO jam_tags (jam_id, tag_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (3, 4),
    (4, 3);

-- Add messages
INSERT INTO jam_message (jam_message_id, jam_id, user_id, jam_message_content, jam_message_timestamp)
VALUES
    (1, 1, 1, 'Welcome to the rock party!', '2025-01-14 20:01:00'),
    (2, 1, 2, 'Thanks for having me!', '2025-01-14 20:02:00'),
    (3, 2, 2, 'Jazz session starting next week', '2025-01-14 10:00:00'),
    (4, 3, 3, 'Thanks everyone for coming', '2025-01-13 17:00:00'),
    (5, 1, 1, 'What should we play next?', '2025-01-14 20:15:00');

-- Update current jam for users in running jams
UPDATE users SET jam_id = 1 WHERE user_id IN (1, 2);