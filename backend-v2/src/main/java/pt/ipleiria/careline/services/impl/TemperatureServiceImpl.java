package pt.ipleiria.careline.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.ipleiria.careline.domain.entities.data.TemperatureEntity;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.domain.enums.Severity;
import pt.ipleiria.careline.exceptions.PatientException;
import pt.ipleiria.careline.exceptions.TemperatureException;
import pt.ipleiria.careline.helpers.TemperatureSeverity;
import pt.ipleiria.careline.repositories.TemperatureRepository;
import pt.ipleiria.careline.services.PatientService;
import pt.ipleiria.careline.services.TemperatureService;
import pt.ipleiria.careline.utils.DateConversionUtil;
import pt.ipleiria.careline.validations.DataValidation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TemperatureServiceImpl implements TemperatureService {
    private final TemperatureRepository temperatureRepository;
    private final PatientService patientService;

    public TemperatureServiceImpl(TemperatureRepository temperatureRepository, PatientService patientService) {
        this.temperatureRepository = temperatureRepository;
        this.patientService = patientService;
        DataValidation dataValidation = new DataValidation();
    }

    private static void temperatureBelongsToPatient(Long patientId, Page<TemperatureEntity> temperatureEntities) {
        if (temperatureEntities.get().anyMatch(temperature -> !temperature.getPatient().getId().equals(patientId))) {
            throw new TemperatureException("Temperature does not belong to patient");
        }
    }

    @Override
    public TemperatureEntity create(Long patientId, TemperatureEntity temperature) {
        if (!DataValidation.isTemperatureValid(temperature.getTemperature())) {
            throw new TemperatureException("Temperature is not valid");
        }

        Optional<PatientEntity> existingPatient = patientService.getPatientById(patientId);
        if (existingPatient.isPresent()) {
            temperature.setPatient(existingPatient.get());
        } else {
            throw new PatientException();
        }

        TemperatureSeverity temperatureSeverity = new TemperatureSeverity();
        Severity severity = temperatureSeverity.getSeverityCategory(temperature.getTemperature());
        temperature.setSeverity(severity);

        return temperatureRepository.save(temperature);
    }

    @Override
    public List<TemperatureEntity> findAll() {
        return StreamSupport.stream(temperatureRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TemperatureEntity> findAll(Pageable pageable, Long patientId) {
        Page<TemperatureEntity> temperatureEntities = temperatureRepository.findAllByPatientId(pageable, patientId);
        temperatureBelongsToPatient(patientId, temperatureEntities);
        return temperatureEntities;
    }

    @Override
    public Page<TemperatureEntity> findAllLatest(Pageable pageable, Long patientId) {
        Page<TemperatureEntity> temperatureEntities = temperatureRepository.findAllByPatientIdOrderByCreatedAtDesc(pageable, patientId);
        temperatureBelongsToPatient(patientId, temperatureEntities);
        return temperatureEntities;
    }

    @Override
    public Optional<TemperatureEntity> getById(Long id) {
        return temperatureRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return temperatureRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        temperatureRepository.deleteById(id);
    }

    @Override
    public Page<TemperatureEntity> findAllByDate(Pageable pageable, Long patientId, String date) {
        DateConversionUtil dateConversionUtil = new DateConversionUtil();
        Instant startDate = dateConversionUtil.convertStringToStartOfDayInstant(date);
        Instant endDate = dateConversionUtil.convertStringToEndOfDayInstant(date);

        Page<TemperatureEntity> temperatureEntities = temperatureRepository.findAllByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(pageable, patientId, startDate, endDate);
        temperatureBelongsToPatient(patientId, temperatureEntities);
        return temperatureEntities;
    }
}
