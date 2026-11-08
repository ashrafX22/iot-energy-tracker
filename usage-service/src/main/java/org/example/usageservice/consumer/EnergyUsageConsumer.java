package org.example.usageservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.usageservice.dto.EnergyUsageDto;
import org.example.usageservice.entity.EnergyReading;
import org.example.usageservice.entity.EnergyReadingId;
import org.example.usageservice.repository.EnergyReadingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
public class EnergyUsageConsumer {

    private final EnergyReadingRepository repository;

    public EnergyUsageConsumer(EnergyReadingRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.energy-usage:energy-usage-events}",
            groupId = "${spring.kafka.consumer.group-id:usage-service-group}"
    )
    public void consume(EnergyUsageDto event) {
        log.info("Received energy usage event from Kafka: meterId={}, usage={} kWh, time={}",
                event.meterId(), event.energyUsage(), event.timestamp());

        try {
            Instant timestamp = parseTimestamp(event.timestamp());
            EnergyReadingId readingId = new EnergyReadingId(event.meterId(), timestamp);
            EnergyReading reading = EnergyReading.builder()
                    .id(readingId)
                    .energyUsage(event.energyUsage())
                    .build();

            repository.save(reading);
            log.info("Successfully persisted telemetry to TimescaleDB for meterId={}", event.meterId());
        } catch (Exception e) {
            log.error("Failed to process and persist event for meterId={}: {}", event.meterId(), e.getMessage(), e);
        }
    }

    private Instant parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) {
            return Instant.now();
        }
        try {
            return Instant.parse(timestampStr);
        } catch (DateTimeParseException e) {
            log.warn("Invalid timestamp format '{}', falling back to Instant.now()", timestampStr);
            return Instant.now();
        }
    }
}
