package org.example.usageservice.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data Projection interface for native SQL query against TimescaleDB continuous aggregate: hourly_energy_usage.
 */
public interface HourlyUsageProjection {
    Instant getBucket();
    UUID getMeterId();
    Double getAvgUsage();
    Double getPeakUsage();
    Double getMinUsage();
    Long getTotalReadings();
}
