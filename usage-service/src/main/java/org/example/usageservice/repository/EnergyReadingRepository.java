package org.example.usageservice.repository;

import org.example.usageservice.dto.HourlyUsageProjection;
import org.example.usageservice.entity.EnergyReading;
import org.example.usageservice.entity.EnergyReadingId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EnergyReadingRepository extends JpaRepository<EnergyReading, EnergyReadingId> {

    // Derived Query Level 1: Find recent readings by meterId with pagination / limit
    List<EnergyReading> findById_MeterIdOrderById_TimeDesc(String meterId, Pageable pageable);

    // Derived Query Level 1: Find readings in a time range
    List<EnergyReading> findById_MeterIdAndId_TimeBetweenOrderById_TimeDesc(String meterId, Instant start, Instant end);

    // Native Query Level 3: Query TimescaleDB Continuous Aggregate view for hourly statistics
    @Query(value = """
        SELECT bucket,
               meter_id AS meterId,
               avg_usage AS avgUsage,
               peak_usage AS peakUsage,
               min_usage AS minUsage,
               total_readings AS totalReadings
        FROM hourly_energy_usage
        WHERE meter_id = :meterId
        ORDER BY bucket DESC
        """, nativeQuery = true)
    List<HourlyUsageProjection> findHourlyUsageByMeterId(@Param("meterId") String meterId);

    // Native Query Level 3: Query TimescaleDB Continuous Aggregate view with time window
    @Query(value = """
        SELECT bucket,
               meter_id AS meterId,
               avg_usage AS avgUsage,
               peak_usage AS peakUsage,
               min_usage AS minUsage,
               total_readings AS totalReadings
        FROM hourly_energy_usage
        WHERE meter_id = :meterId
          AND bucket >= :start
          AND bucket <= :end
        ORDER BY bucket DESC
        """, nativeQuery = true)
    List<HourlyUsageProjection> findHourlyUsageByMeterIdAndBucketBetween(
        @Param("meterId") String meterId,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
}
