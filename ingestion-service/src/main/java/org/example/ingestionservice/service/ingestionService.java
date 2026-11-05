package org.example.ingestionservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.ingestionservice.dto.energyUsageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ingestionService {

    private final KafkaTemplate<String, energyUsageDto> kafkaTemplate;
    private final String topicName;

    public ingestionService(KafkaTemplate<String, energyUsageDto> kafkaTemplate,
                            @Value("${app.kafka.topic.energy-usage:energy-usage-events}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public CompletableFuture<SendResult<String, energyUsageDto>> sendEnergyUsage(energyUsageDto energyUsage) {
        log.info("Publishing energy usage event for meterId '{}' to topic '{}'", energyUsage.meterId(), topicName);
        CompletableFuture<SendResult<String, energyUsageDto>> future = kafkaTemplate.send(topicName, energyUsage.meterId(), energyUsage);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published energy usage for meterId '{}' to partition {} with offset {}",
                        energyUsage.meterId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish energy usage for meterId '{}': {}",
                        energyUsage.meterId(), ex.getMessage(), ex);
            }
        });

        return future;
    }
}
