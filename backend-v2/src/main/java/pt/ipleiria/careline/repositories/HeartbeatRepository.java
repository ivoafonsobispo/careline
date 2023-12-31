package pt.ipleiria.careline.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pt.ipleiria.careline.domain.entities.data.HeartbeatEntity;

import java.time.Instant;

@Repository
public interface HeartbeatRepository extends JpaRepository<HeartbeatEntity, Long>,
        PagingAndSortingRepository<HeartbeatEntity, Long> {
    Page<HeartbeatEntity> findAllByPatientId(Pageable pageable, Long patientId);

    Page<HeartbeatEntity> findAllByPatientIdOrderByCreatedAtDesc(Pageable pageable, Long patientId);

    Page<HeartbeatEntity> findAllByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(Pageable pageable, Long patientId, Instant startDate, Instant endDate);
}
