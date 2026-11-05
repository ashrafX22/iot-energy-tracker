package org.example.ingestionservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.ingestionservice.dto.energyUsageDto;
import org.example.ingestionservice.service.ingestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ingestion")
@Slf4j
public class ingestionController {

    private final ingestionService ingestionService;

    public ingestionController(ingestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/energy")
    public ResponseEntity<Map<String, Object>> ingestEnergyUsage(@RequestBody energyUsageDto energyUsage) {
        log.info("Received energy ingestion request for meterId: {}", energyUsage.meterId());
        ingestionService.sendEnergyUsage(energyUsage);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "status", "ACCEPTED",
                "message", "Energy usage event queued for publishing",
                "meterId", energyUsage.meterId()
        ));
    }
}
