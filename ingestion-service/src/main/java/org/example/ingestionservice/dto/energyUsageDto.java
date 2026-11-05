package org.example.ingestionservice.dto;

public record energyUsageDto(
        String meterId,
        String timestamp,
        double energyUsage
) {
}
