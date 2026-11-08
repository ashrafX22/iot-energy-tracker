package org.example.usageservice.repository;

import org.example.usageservice.entity.EnergyReading;
import org.example.usageservice.entity.EnergyReadingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EnergyReadingRepository extends JpaRepository<EnergyReading, EnergyReadingId> {

    List<EnergyReading> findById_MeterIdOrderById_TimeDesc(String meterId);

    List<EnergyReading> findById_MeterIdAndId_TimeBetweenOrderById_TimeDesc(String meterId, Instant start, Instant end);
}
