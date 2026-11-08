package org.example.usageservice.dto;

import java.time.Instant;

/**
 * Spring Data Projection interface for native SQL query against TimescaleDB continuous aggregate: hourly_energy_usage.
 */
public interface HourlyUsageProjection {
    Instant getBucket();
    String getMeterId();
    Double getAvgUsage();
    Double getPeakUsage();
    Double getMinUsage();
    Long getTotalReadings();
}
