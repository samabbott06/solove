-- Insert a test user for development
INSERT INTO user_profile (id, external_id, email, display_name, created_at, updated_at)
VALUES (
  '00000000-0000-0000-0000-000000000001',
  'test-user-external-id',
  'test@solove.me',
  'Test User',
  now(),
  now()
) ON CONFLICT (external_id) DO NOTHING;