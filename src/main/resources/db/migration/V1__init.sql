-- user profiles (one row per authenticated identity)
create extension if not exists pgcrypto; -- for gen_random_uuid() if needed

create table if not exists user_profile (
  id uuid primary key default gen_random_uuid(),
  external_id text not null unique,               -- Google 'sub' (stable per project)
  email text not null unique,
  display_name text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create or replace function set_updated_at()
returns trigger language plpgsql as $$
begin
  new.updated_at = now();
  return new;
end $$;

drop trigger if exists trg_user_profile_updated_at on user_profile;
create trigger trg_user_profile_updated_at
before update on user_profile
for each row execute function set_updated_at();
