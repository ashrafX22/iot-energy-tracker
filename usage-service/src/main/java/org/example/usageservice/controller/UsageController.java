package org.example.usageservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usageservice.dto.EnergyReadingResponseDto;
import org.example.usageservice.dto.HourlyUsageResponseDto;
import org.example.usageservice.dto.UserUsageResponseDto;
import org.example.usageservice.service.UsageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usage")
@RequiredArgsConstructor
@Slf4j
public class UsageController {

    private final UsageService usageService;

    // =========================================================================
    // Meter-level endpoints (single device)
    // =========================================================================

    /**
     * Get raw telemetry history for a specific meter.
     * Optional: filter by time window (start + end) or bound by limit (default 50).
     */
    @GetMapping("/{meterId}/history")
    public ResponseEntity<List<EnergyReadingResponseDto>> getHistory(
            @PathVariable UUID meterId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        if (start != null && end != null) {
            return ResponseEntity.ok(usageService.getReadingsBetween(meterId, start, end));
        }
        return ResponseEntity.ok(usageService.getRecentReadings(meterId, limit));
    }

    /**
     * Get pre-computed hourly aggregated stats for a specific meter.
     * Backed by TimescaleDB continuous aggregate view (hourly_energy_usage).
     */
    @GetMapping("/{meterId}/hourly")
    public ResponseEntity<List<HourlyUsageResponseDto>> getHourlyStats(
            @PathVariable UUID meterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        if (start != null && end != null) {
            return ResponseEntity.ok(usageService.getHourlyStatsBetween(meterId, start, end));
        }
        return ResponseEntity.ok(usageService.getHourlyStats(meterId));
    }

    /**
     * Get complete energy usage for ALL devices belonging to a user.
     * <p>
     * Internally: calls device-service to resolve userId → deviceIds,
     * then queries TimescaleDB for readings and hourly stats per device.
     * </p>
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserUsageResponseDto> getUserUsage(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "20") int limitPerDevice
    ) {
        log.info("GET /usage/users/{} called", userId);
        return ResponseEntity.ok(usageService.getUserUsage(userId, limitPerDevice));
    }
}

