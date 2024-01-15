package pt.ipleiria.careline.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.ipleiria.careline.domain.entities.data.HeartbeatEntity;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.domain.enums.Severity;
import pt.ipleiria.careline.exceptions.HeartbeatException;
import pt.ipleiria.careline.exceptions.PatientException;
import pt.ipleiria.careline.helpers.HeartbeatSeverity;
import pt.ipleiria.careline.repositories.HeartbeatRepository;
import pt.ipleiria.careline.services.HeartbeatService;
import pt.ipleiria.careline.services.PatientService;
import pt.ipleiria.careline.utils.DateConversionUtil;
import pt.ipleiria.careline.validations.DataValidation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class HeartbeatServiceImpl implements HeartbeatService {
    private final HeartbeatRepository heartbeatRepository;
    private final PatientService patientService;

    public HeartbeatServiceImpl(HeartbeatRepository heartbeatRepository, PatientService patientService) {
        this.heartbeatRepository = heartbeatRepository;
        this.patientService = patientService;
    }

    private static void heartbeatBelongsToPatient(Long patientId, Page<HeartbeatEntity> heartbeatEntities) {
        if (heartbeatEntities.get().anyMatch(heartbeat -> !heartbeat.getPatient().getId().equals(patientId))) {
            log.atError().log("Heartbeat does not belong to patient - {}", patientId);
            throw new HeartbeatException("Heartbeat does not belong to patient");
        }
    }

    @Override
    public HeartbeatEntity create(Long patientId, HeartbeatEntity heartbeatEntity) {
        if (!DataValidation.isHeartbeatValid(heartbeatEntity.getHeartbeat())) {
            log.atError().log("Invalid heartbeat - {}", heartbeatEntity.getHeartbeat());
            throw new HeartbeatException();
        }

        Optional<PatientEntity> existingPatient = patientService.getPatientById(patientId);
        if (existingPatient.isEmpty()) {
            log.atError().log("Patient does not exist - {}", patientId);
            throw new PatientException();
        }

        heartbeatEntity.setPatient(existingPatient.get());
        HeartbeatSeverity heartbeatSeverity = new HeartbeatSeverity();
        Severity severity = heartbeatSeverity.getSeverityCategory(heartbeatEntity.getHeartbeat());
        heartbeatEntity.setSeverity(severity);

        log.atInfo().log("Created Heartbeat - {}", heartbeatEntity);
        return heartbeatRepository.save(heartbeatEntity);
    }

    @Override
    public List<HeartbeatEntity> findAll() {
        log.atInfo().log("Find all Heartbeats");
        return StreamSupport.stream(heartbeatRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<HeartbeatEntity> findAll(Pageable pageable, Long patientId) {
        Page<HeartbeatEntity> heartbeatEntities = heartbeatRepository.findAllByPatientId(pageable, patientId);
        heartbeatBelongsToPatient(patientId, heartbeatEntities);

        log.atInfo().log("Find all Heartbeats by patient id - {}", patientId);

        return heartbeatEntities;
    }

    @Override
    public Page<HeartbeatEntity> findAllLatest(Pageable pageable, Long patientId) {
        log.atInfo().log("Find all latest Heartbeats by patient id - {}", patientId);
        return heartbeatRepository.findAllByPatientIdOrderByCreatedAtDesc(pageable, patientId);
    }

    @Override
    public Optional<HeartbeatEntity> getById(Long id) {
        log.atInfo().log("Get Heartbeat by id - {}", id);
        return heartbeatRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        log.atInfo().log("Check if Heartbeat exists - {}", id);
        return heartbeatRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        log.atInfo().log("Delete Heartbeat by id - {}", id);
        heartbeatRepository.deleteById(id);
    }

    @Override
    public Page<HeartbeatEntity> findAllByDate(Pageable pageable, Long patientId, String date) {
        DateConversionUtil dateConversionUtil = new DateConversionUtil();
        Instant startDate = dateConversionUtil.convertStringToStartOfDayInstant(date);
        Instant endDate = dateConversionUtil.convertStringToEndOfDayInstant(date);

        Page<HeartbeatEntity> heartbeatEntities = heartbeatRepository.findAllByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                pageable, patientId, startDate, endDate);
        heartbeatBelongsToPatient(patientId, heartbeatEntities);

        log.atInfo().log("Find all Heartbeats by patient id - {} and date - {}", patientId, date);
        return heartbeatEntities;
    }
}
