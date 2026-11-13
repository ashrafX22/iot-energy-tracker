package org.example.usageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usageservice.client.device.DeviceClient;
import org.example.usageservice.client.device.dto.DeviceResponseDto;
import org.example.usageservice.dto.*;
import org.example.usageservice.repository.EnergyReadingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsageService {

    private final EnergyReadingRepository repository;
    private final DeviceClient deviceClient;

    /**
     * Get recent raw telemetry readings for a specific meter (bounded by limit).
     */
    public List<EnergyReadingResponseDto> getRecentReadings(UUID meterId, int limit) {
        int boundedLimit = Math.min(Math.max(limit, 1), 500);
        Pageable pageable = PageRequest.of(0, boundedLimit);
        log.debug("Fetching last {} readings for meter: {}", boundedLimit, meterId);
        return repository.findById_MeterIdOrderById_TimeDesc(meterId, pageable)
                .stream()
                .map(EnergyReadingResponseDto::fromEntity)
                .toList();
    }

    /**
     * Get raw readings within a specific time range for a meter.
     */
    public List<EnergyReadingResponseDto> getReadingsBetween(UUID meterId, Instant start, Instant end) {
        log.debug("Fetching readings for meter: {} between {} and {}", meterId, start, end);
        return repository.findById_MeterIdAndId_TimeBetweenOrderById_TimeDesc(meterId, start, end)
                .stream()
                .map(EnergyReadingResponseDto::fromEntity)
                .toList();
    }

    /**
     * Get pre-aggregated hourly statistics from TimescaleDB continuous aggregate for a meter.
     */
    public List<HourlyUsageResponseDto> getHourlyStats(UUID meterId) {
        log.debug("Fetching hourly rollups for meter: {}", meterId);
        return repository.findHourlyUsageByMeterId(meterId)
                .stream()
                .map(HourlyUsageResponseDto::fromProjection)
                .toList();
    }

    /**
     * Get hourly statistics within a time window for a meter.
     */
    public List<HourlyUsageResponseDto> getHourlyStatsBetween(UUID meterId, Instant start, Instant end) {
        log.debug("Fetching hourly rollups for meter: {} between {} and {}", meterId, start, end);
        return repository.findHourlyUsageByMeterIdAndBucketBetween(meterId, start, end)
                .stream()
                .map(HourlyUsageResponseDto::fromProjection)
                .toList();
    }

    /**
     * Get complete energy usage for ALL devices belonging to a user.
     * <p>
     * Flow:
     * 1. Calls device-service via @HttpExchange DeviceClient to resolve userId → List<deviceId>
     * 2. For each device, fetches recent readings and hourly stats from TimescaleDB
     * 3. Assembles and returns a unified UserUsageResponseDto
     * </p>
     */
    public UserUsageResponseDto getUserUsage(UUID userId, int limitPerDevice) {
        log.info("Fetching usage for all devices of userId: {}", userId);

        // Step 1: Call device-service (HTTP via @HttpExchange proxy)
        List<DeviceResponseDto> devices = deviceClient.getDevicesByUserId(userId);
        log.debug("Resolved {} devices for userId: {}", devices.size(), userId);

        int boundedLimit = Math.min(Math.max(limitPerDevice, 1), 100);
        Pageable pageable = PageRequest.of(0, boundedLimit);
        List<UUID> devicesIDs = devices.stream().map(device->device.id()).toList();

        // Step 2: For each device fetch usage from TimescaleDB and build nested DTO
        List<UserUsageResponseDto.DeviceUsageDto> deviceUsages = devices.stream()
                .map(device -> {
                    UUID meterId = device.id(); // device UUID is the meterId

                    List<EnergyReadingResponseDto> readings =
                            repository.findById_MeterIdOrderById_TimeDesc(meterId, pageable)
                                    .stream()
                                    .map(EnergyReadingResponseDto::fromEntity)
                                    .toList();

                    List<HourlyUsageResponseDto> hourly =
                            repository.findHourlyUsageByMeterId(meterId)
                                    .stream()
                                    .map(HourlyUsageResponseDto::fromProjection)
                                    .toList();

                    return UserUsageResponseDto.DeviceUsageDto.of(device, readings, hourly);
                })
                .toList();

        // Step 3: Assemble the top-level user response
        return new UserUsageResponseDto(userId, devices.size(), deviceUsages);
    }
}
