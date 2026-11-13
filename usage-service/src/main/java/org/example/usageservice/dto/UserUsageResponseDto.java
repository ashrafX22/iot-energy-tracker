package org.example.usageservice.dto;

import org.example.usageservice.client.device.dto.DeviceResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for GET /usage/users/{userId}.
 * Aggregates device metadata (from device-service) with usage history (from TimescaleDB).
 */
public record UserUsageResponseDto(
    UUID userId,
    int totalDevices,
    List<DeviceUsageDto> devices
) {
    /**
     * Per-device usage summary nested inside UserUsageResponseDto.
     */
    public record DeviceUsageDto(
        UUID deviceId,
        String deviceName,
        String deviceType,
        String location,
        List<EnergyReadingResponseDto> recentReadings,
        List<HourlyUsageResponseDto> hourlyStats
    ) {
        public static DeviceUsageDto of(DeviceResponseDto device,
                                        List<EnergyReadingResponseDto> readings,
                                        List<HourlyUsageResponseDto> hourly) {
            return new DeviceUsageDto(
                device.id(),
                device.name(),
                device.type(),
                device.location(),
                readings,
                hourly
            );
        }
    }
}
