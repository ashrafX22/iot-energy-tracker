package org.example.usageservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usageservice.dto.EnergyReadingResponseDto;
import org.example.usageservice.dto.HourlyUsageResponseDto;
import org.example.usageservice.service.UsageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/usage")
@RequiredArgsConstructor
@Slf4j
public class UsageController {

    private final UsageService usageService;

    /**
     * Get raw telemetry history for a meter.
     * Optionally filtered by time window or bounded by limit (default 50).
     */
    @GetMapping("/{meterId}/history")
    public ResponseEntity<List<EnergyReadingResponseDto>> getHistory(
            @PathVariable String meterId,
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
     * Get pre-computed hourly aggregated stats for charts, analytics, and billing.
     * Backed by TimescaleDB continuous aggregate view (hourly_energy_usage).
     */
    @GetMapping("/{meterId}/hourly")
    public ResponseEntity<List<HourlyUsageResponseDto>> getHourlyStats(
            @PathVariable String meterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        if (start != null && end != null) {
            return ResponseEntity.ok(usageService.getHourlyStatsBetween(meterId, start, end));
        }
        return ResponseEntity.ok(usageService.getHourlyStats(meterId));
    }
}
