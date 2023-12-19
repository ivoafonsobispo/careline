package pt.ipleiria.careline.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ipleiria.careline.domain.entities.DiagnosisEntity;

import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long>,
        PagingAndSortingRepository<DiagnosisEntity, Long> {
    @Query("SELECT d FROM DiagnosisEntity d WHERE d.id = :id AND d.patient.id = :patientId")
    Optional<DiagnosisEntity> findByIdOfPatient(@Param("patientId") Long patientId, @Param("id") Long id);

    @Query("SELECT d FROM DiagnosisEntity d WHERE d.patient.id = :patientId")
    Page<DiagnosisEntity> findAllByPatientId(@Param("patientId") Long patientId,
                                             Pageable pageable);
}
