package org.example.usageservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnergyReadingId implements Serializable {

    @Column(name = "meter_id", nullable = false, length = 100)
    private String meterId;

    @Column(name = "time", nullable = false)
    private Instant time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyReadingId that = (EnergyReadingId) o;
        return Objects.equals(meterId, that.meterId) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meterId, time);
    }
}
