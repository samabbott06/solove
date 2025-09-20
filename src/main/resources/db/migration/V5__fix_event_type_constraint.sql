-- Fix event type constraint to match Java enum values
ALTER TABLE chat_event DROP CONSTRAINT chat_event_event_type_check;
ALTER TABLE chat_event ADD CONSTRAINT chat_event_event_type_check
  CHECK (event_type IN ('USER_MESSAGE', 'ASSISTANT_MESSAGE', 'SESSION_START', 'SESSION_END', 'FUNCTION_CALL', 'ERROR'));