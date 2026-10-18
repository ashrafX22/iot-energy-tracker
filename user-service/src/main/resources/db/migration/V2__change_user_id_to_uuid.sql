-- =============================================================
-- V2__change_user_id_to_uuid.sql
-- Migrate users table primary key from BIGINT to UUID
-- =============================================================

-- 1. Add new UUID column with default generator for existing/new records
ALTER TABLE users ADD COLUMN new_id UUID DEFAULT gen_random_uuid() NOT NULL;

-- 2. Drop the existing primary key constraint
ALTER TABLE users DROP CONSTRAINT users_pkey;

-- 3. Drop the old BIGINT id column
ALTER TABLE users DROP COLUMN id;

-- 4. Rename new_id to id
ALTER TABLE users RENAME COLUMN new_id TO id;

-- 5. Re-apply the primary key constraint on the new UUID id column
ALTER TABLE users ADD PRIMARY KEY (id);
