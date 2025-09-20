-- Fix session type constraint to match Java enum values
ALTER TABLE chat_session DROP CONSTRAINT chat_session_session_type_check;
ALTER TABLE chat_session ADD CONSTRAINT chat_session_session_type_check
  CHECK (session_type IN ('TEXT', 'VOICE'));