package pt.ipleiria.careline.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.ipleiria.careline.domain.entities.data.HeartbeatEntity;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.helpers.DataValidation;
import pt.ipleiria.careline.repositories.HeartbeatRepository;
import pt.ipleiria.careline.services.HeartbeatService;
import pt.ipleiria.careline.services.PatientService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class HeartbeatServiceImpl implements HeartbeatService {

    private HeartbeatRepository heartbeatRepository;
    private PatientService patientService;

    public HeartbeatServiceImpl(HeartbeatRepository heartbeatRepository, PatientService patientService) {
        this.heartbeatRepository = heartbeatRepository;
        this.patientService = patientService;
    }

    @Override
    public HeartbeatEntity create(Long patientId, HeartbeatEntity heartbeatEntity) {
        if (!DataValidation.isHeartbeatValid(heartbeatEntity.getHeartbeat())) {
            throw new IllegalArgumentException("Heartbeat is not valid");
        }

        Optional<PatientEntity> existingPatient = patientService.getPatientById(patientId);
        if (existingPatient.isPresent()) {
            heartbeatEntity.setPatient(existingPatient.get());
        } else {
            throw new IllegalArgumentException("Patient not found");
        }

        return heartbeatRepository.save(heartbeatEntity);
    }

    @Override
    public List<HeartbeatEntity> findAll() {
        return StreamSupport.stream(heartbeatRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<HeartbeatEntity> findAll(Pageable pageable, Long patientId) {
        return heartbeatRepository.findAllByPatientId(pageable, patientId);
    }

    @Override
    public Page<HeartbeatEntity> findAllLatest(Pageable pageable, Long patientId) {
        return heartbeatRepository.findAllByPatientIdOrderByCreatedAtDesc(pageable, patientId);
    }

    @Override
    public Optional<HeartbeatEntity> getById(Long id) {
        return heartbeatRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return heartbeatRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        heartbeatRepository.deleteById(id);
    }


}