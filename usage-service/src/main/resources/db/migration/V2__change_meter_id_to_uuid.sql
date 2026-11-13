-- =================================================================
-- V2: Change meter_id column from VARCHAR(100) to UUID
-- =================================================================

-- 1. Drop continuous aggregate view dependent on meter_id
DROP MATERIALIZED VIEW IF EXISTS hourly_energy_usage CASCADE;

-- 2. Alter column type in hypertable
ALTER TABLE energy_readings ALTER COLUMN meter_id TYPE UUID USING meter_id::uuid;

-- 3. Re-create Continuous Aggregate for hourly rollups
CREATE MATERIALIZED VIEW IF NOT EXISTS hourly_energy_usage
WITH (timescaledb.continuous) AS
SELECT 
    time_bucket('1 hour', time) AS bucket,
    meter_id,
    AVG(energy_usage) AS avg_usage,
    MAX(energy_usage) AS peak_usage,
    MIN(energy_usage) AS min_usage,
    COUNT(*)          AS total_readings
FROM energy_readings
GROUP BY bucket, meter_id
WITH NO DATA;

-- 4. Re-add background refresh policy for hourly continuous aggregate
SELECT add_continuous_aggregate_policy('hourly_energy_usage',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '15 minutes',
    if_not_exists => TRUE
);
