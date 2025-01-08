
-- Insert data before tests

-- Insert Users
INSERT INTO users (user_id, user_name, user_email, user_country, user_img_url, user_provider, user_provider_id, user_has_jam_running)
VALUES (1, 'test-user', 'test-user@example.com', 'FR', 'http://example.com/img.jpg', 'spotify', '1234567890', false);
INSERT INTO users (user_id, user_name, user_email, user_country, user_img_url, user_provider, user_provider_id, user_has_jam_running)
VALUES (2, 'test-user-2', 'test-expired-user@example.com', 'FR', 'http://example.com/img.jpg', 'amazon', '0987654321', false);

-- Insert Roles
INSERT INTO user_roles (user_id, user_role)
VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, user_role)
VALUES (2, 'ROLE_USER');

-- Insert Access Tokens
-- valid token
INSERT INTO user_access_tokens (id, user_user_id, access_token, provider, expires_at)
VALUES (1, 1, 'test-access-token', 'spotify', '2030-01-01 00:00:00');
-- expired token
INSERT INTO user_access_tokens (id, user_user_id, access_token, provider, expires_at)
VALUES (2, 2, 'test-expired-access-token', 'amazon', '2020-01-01 00:00:00');