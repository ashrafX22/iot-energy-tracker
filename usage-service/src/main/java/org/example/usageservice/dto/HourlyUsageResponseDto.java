package org.example.usageservice.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * REST Response DTO for hourly aggregated energy consumption.
 */
public record HourlyUsageResponseDto(
    Instant bucket,
    UUID meterId,
    Double avgUsage,
    Double peakUsage,
    Double minUsage,
    Long totalReadings
) {
    public static HourlyUsageResponseDto fromProjection(HourlyUsageProjection projection) {
        return new HourlyUsageResponseDto(
            projection.getBucket(),
            projection.getMeterId(),
            projection.getAvgUsage(),
            projection.getPeakUsage(),
            projection.getMinUsage(),
            projection.getTotalReadings()
        );
    }
}
