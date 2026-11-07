-- =================================================================
-- V1: Initialize Energy Telemetry Schema & TimescaleDB Hypertables
-- =================================================================

-- 1. Ensure TimescaleDB extension is active
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 2. Raw energy readings table
CREATE TABLE IF NOT EXISTS energy_readings (
    time         TIMESTAMPTZ      NOT NULL,
    meter_id     VARCHAR(100)     NOT NULL,
    energy_usage DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_energy_readings PRIMARY KEY (meter_id, time)
);

-- 3. Convert table to a TimescaleDB Hypertable partitioned by time (1-day chunk interval)
SELECT create_hypertable(
    'energy_readings',
    'time',
    chunk_time_interval => INTERVAL '1 day',
    if_not_exists => TRUE
);

-- 4. Composite index for fast queries by meterId ordered by time
CREATE INDEX IF NOT EXISTS idx_energy_readings_meter_time 
ON energy_readings (meter_id, time DESC);

-- 5. Enable Columnar Compression for historical data
ALTER TABLE energy_readings SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'meter_id',
    timescaledb.compress_orderby = 'time DESC'
);

-- 6. Automated compression policy for chunks older than 7 days
SELECT add_compression_policy('energy_readings', INTERVAL '7 days', if_not_exists => TRUE);

-- 7. Continuous Aggregate for hourly rollups
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

-- 8. Background refresh policy for hourly continuous aggregate
SELECT add_continuous_aggregate_policy('hourly_energy_usage',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '15 minutes',
    if_not_exists => TRUE
);
