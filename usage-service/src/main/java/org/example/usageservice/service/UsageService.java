package org.example.usageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usageservice.dto.EnergyReadingResponseDto;
import org.example.usageservice.dto.HourlyUsageResponseDto;
import org.example.usageservice.repository.EnergyReadingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsageService {

    private final EnergyReadingRepository repository;

    /**
     * Get recent raw telemetry readings for a specific meter.
     */
    public List<EnergyReadingResponseDto> getRecentReadings(String meterId, int limit) {
        int boundedLimit = Math.min(Math.max(limit, 1), 500); // cap between 1 and 500
        Pageable pageable = PageRequest.of(0, boundedLimit);
        log.debug("Fetching last {} readings for meter: {}", boundedLimit, meterId);
        return repository.findById_MeterIdOrderById_TimeDesc(meterId, pageable)
                .stream()
                .map(EnergyReadingResponseDto::fromEntity)
                .toList();
    }

    /**
     * Get raw readings within a specific time range.
     */
    public List<EnergyReadingResponseDto> getReadingsBetween(String meterId, Instant start, Instant end) {
        log.debug("Fetching readings for meter: {} between {} and {}", meterId, start, end);
        return repository.findById_MeterIdAndId_TimeBetweenOrderById_TimeDesc(meterId, start, end)
                .stream()
                .map(EnergyReadingResponseDto::fromEntity)
                .toList();
    }

    /**
     * Get pre-aggregated hourly statistics from TimescaleDB continuous aggregate.
     */
    public List<HourlyUsageResponseDto> getHourlyStats(String meterId) {
        log.debug("Fetching hourly rollups for meter: {}", meterId);
        return repository.findHourlyUsageByMeterId(meterId)
                .stream()
                .map(HourlyUsageResponseDto::fromProjection)
                .toList();
    }

    /**
     * Get hourly statistics within a time window.
     */
    public List<HourlyUsageResponseDto> getHourlyStatsBetween(String meterId, Instant start, Instant end) {
        log.debug("Fetching hourly rollups for meter: {} between {} and {}", meterId, start, end);
        return repository.findHourlyUsageByMeterIdAndBucketBetween(meterId, start, end)
                .stream()
                .map(HourlyUsageResponseDto::fromProjection)
                .toList();
    }
}
