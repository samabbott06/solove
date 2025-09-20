-- Chat functionality tables

-- Chat sessions (text or voice conversations)
create table if not exists chat_session (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references user_profile(id) on delete cascade,
  session_type varchar(10) not null check (session_type in ('text', 'voice')),
  started_at timestamptz not null default now(),
  ended_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Chat events (all interactions within a session)
create table if not exists chat_event (
  id uuid primary key default gen_random_uuid(),
  session_id uuid not null references chat_session(id) on delete cascade,
  event_type varchar(20) not null check (event_type in ('user_message', 'assistant_message', 'session_start', 'session_end', 'function_call', 'error')),
  content text,
  metadata jsonb, -- for storing additional context like function call parameters, error details, etc.
  created_at timestamptz not null default now()
);

-- Indexes for performance
create index idx_chat_session_user_id_started_at on chat_session(user_id, started_at desc);
create index idx_chat_event_session_id_created_at on chat_event(session_id, created_at);
create index idx_chat_event_type on chat_event(event_type);

-- Trigger for updating chat_session.updated_at
drop trigger if exists trg_chat_session_updated_at on chat_session;
create trigger trg_chat_session_updated_at
before update on chat_session
for each row execute function set_updated_at();