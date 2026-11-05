package org.example.ingestionservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.energy-usage:energy-usage-events}")
    private String energyUsageTopic;

    @Bean
    public NewTopic energyUsageTopic() {
        return TopicBuilder.name(energyUsageTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
