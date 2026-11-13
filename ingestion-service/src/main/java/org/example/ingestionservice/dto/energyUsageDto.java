package org.example.ingestionservice.dto;

import java.util.UUID;

public record energyUsageDto(
        UUID meterId,
        String timestamp,
        double energyUsage
) {
}
