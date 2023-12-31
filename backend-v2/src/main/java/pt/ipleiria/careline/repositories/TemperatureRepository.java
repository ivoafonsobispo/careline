package pt.ipleiria.careline.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pt.ipleiria.careline.domain.entities.data.TemperatureEntity;

import java.time.Instant;

@Repository
public interface TemperatureRepository extends JpaRepository<TemperatureEntity, Long>,
        PagingAndSortingRepository<TemperatureEntity, Long> {
    Page<TemperatureEntity> findAllByPatientId(Pageable pageable, Long patientId);

    Page<TemperatureEntity> findAllByPatientIdOrderByCreatedAtDesc(Pageable pageable, Long patientId);

    Page<TemperatureEntity> findAllByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(Pageable pageable, Long patientId, Instant startDate, Instant endDate);
}
