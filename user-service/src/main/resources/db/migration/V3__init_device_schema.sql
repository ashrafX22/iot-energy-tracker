-- =============================================================
-- V3__init_device_schema.sql
-- Create devices table schema
-- =============================================================

DO $$ BEGIN
    CREATE TYPE device_type AS ENUM ('SMART_METER', 'SENSOR', 'ACTUATOR', 'THERMOSTAT', 'APPLIANCE', 'OTHER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    type device_type NOT NULL,
    location VARCHAR(255),
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_devices_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_devices_user_id ON devices(user_id);
CREATE INDEX IF NOT EXISTS idx_devices_type ON devices(type);
