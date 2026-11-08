package org.example.usageservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "energy_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyReading {

    @EmbeddedId
    private EnergyReadingId id;

    @Column(name = "energy_usage", nullable = false)
    private double energyUsage;
}
