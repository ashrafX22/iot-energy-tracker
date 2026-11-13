package org.example.usageservice.dto;

import org.example.usageservice.entity.EnergyReading;

import java.time.Instant;
import java.util.UUID;

/**
 * REST Response DTO for raw energy readings.
 */
public record EnergyReadingResponseDto(
    UUID meterId,
    Instant timestamp,
    double energyUsage
) {
    public static EnergyReadingResponseDto fromEntity(EnergyReading reading) {
        return new EnergyReadingResponseDto(
            reading.getId().getMeterId(),
            reading.getId().getTime(),
            reading.getEnergyUsage()
        );
    }
}
