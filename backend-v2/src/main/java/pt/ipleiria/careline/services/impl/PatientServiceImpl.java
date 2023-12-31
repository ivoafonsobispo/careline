package pt.ipleiria.careline.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.domain.entities.users.ProfessionalEntity;
import pt.ipleiria.careline.exceptions.PatientException;
import pt.ipleiria.careline.repositories.PatientRepository;
import pt.ipleiria.careline.services.PatientService;
import pt.ipleiria.careline.validations.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientEntity save(PatientEntity patientEntity) {
        validatePatient(patientEntity);
        return patientRepository.save(patientEntity);
    }

    @Override
    public Optional<PatientEntity> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<PatientEntity> getPatientByNus(String nus) {
        return patientRepository.findByNus(nus);
    }

    @Override
    public List<PatientEntity> findAll() {
        return StreamSupport.stream(patientRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PatientEntity> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    @Override
    public boolean isExists(Long id) {
        return patientRepository.existsById(id);
    }

    @Override
    public PatientEntity partialUpdate(Long id, PatientEntity patientEntity) {
        patientEntity.setId(id);
        return patientRepository.findById(id).map(existingPatient -> {
            Optional.ofNullable(patientEntity.getName()).ifPresent(existingPatient::setName);
            Optional.ofNullable(patientEntity.getEmail()).ifPresent(existingPatient::setEmail);
            Optional.ofNullable(patientEntity.getPassword()).ifPresent(existingPatient::setPassword);
            Optional.ofNullable(patientEntity.getNus()).ifPresent(existingPatient::setNus);
            Optional.ofNullable(patientEntity.getProfessionals()).ifPresent(existingPatient::setProfessionals);
            return patientRepository.save(existingPatient);
        }).orElseThrow(PatientException::new);
    }


    @Override
    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public void setProfessionalToPatient(ProfessionalEntity professional, PatientEntity patient) {
        patient.getProfessionals().add(professional);
        patientRepository.save(patient);
    }


    private void validatePatient(PatientEntity patientEntity) {
        List<String> errors = new ArrayList<>();
        if (patientRepository.findByNus(patientEntity.getNus()).isPresent())
            errors.add("NUS already exists");
        if (patientRepository.findByEmail(patientEntity.getEmail()).isPresent())
            errors.add("Email already exists");
        if (!UserValidation.isNusValid(patientEntity.getNus()))
            errors.add("Invalid NUS");
        if (!errors.isEmpty())
            throw new IllegalArgumentException(String.join(", ", errors));
    }
}
