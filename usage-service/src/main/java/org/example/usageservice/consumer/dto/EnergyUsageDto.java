package org.example.usageservice.consumer.dto;

import java.util.UUID;

public record EnergyUsageDto(
        UUID meterId,
        String timestamp,
        double energyUsage
) {
}
