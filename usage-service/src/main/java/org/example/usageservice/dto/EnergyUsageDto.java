package org.example.usageservice.dto;

public record EnergyUsageDto(
        String meterId,
        String timestamp,
        double energyUsage
) {
}
