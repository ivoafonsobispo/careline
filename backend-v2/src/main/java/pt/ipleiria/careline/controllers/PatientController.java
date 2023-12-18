package pt.ipleiria.careline.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipleiria.careline.domain.dto.PatientDTO;
import pt.ipleiria.careline.domain.dto.responses.PatientResponseDTO;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.mappers.Mapper;
import pt.ipleiria.careline.services.PatientService;

import java.util.Optional;

@RequestMapping("/api/patients")
@RestController
public class PatientController {

    private PatientService patientService;
    private Mapper<PatientEntity, PatientDTO> patientMapper;
    private Mapper<PatientEntity, PatientResponseDTO> patientResponseMapper;

    public PatientController(PatientService patientService, Mapper<PatientEntity, PatientDTO> patientMapper, Mapper<PatientEntity, PatientResponseDTO> patientResponseMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.patientResponseMapper = patientResponseMapper;
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> create(@RequestBody @Valid PatientDTO patientDTO) {
        PatientEntity patientEntity = patientMapper.mapFrom(patientDTO);
        PatientEntity savedPatientEntity = patientService.save(patientEntity);
        return new ResponseEntity<>(patientResponseMapper.mapToDTO(savedPatientEntity), HttpStatus.CREATED);
    }

    @GetMapping
    public Page<PatientDTO> listPatients(Pageable pageable) {
        Page<PatientEntity> patients = patientService.findAll(pageable);
        return patients.map(patientMapper::mapToDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id) {
        Optional<PatientEntity> patient = patientService.getPatientById(id);
        return patient.map(patientEntity -> {
            PatientDTO patientDTO = patientMapper.mapToDTO(patientEntity);
            return new ResponseEntity<>(patientDTO, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/nus/{nus}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("nus") String nus) {
        Optional<PatientEntity> patient = patientService.getPatientByNus(nus);
        return patient.map(patientEntity -> {
            PatientDTO patientDTO = patientMapper.mapToDTO(patientEntity);
            return new ResponseEntity<>(patientDTO, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> fullUpdatePatient(@PathVariable("id") Long id, @RequestBody @Valid PatientDTO patientDTO) {
        if (!patientService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        patientDTO.setId(id);
        PatientEntity patientEntity = patientMapper.mapFrom(patientDTO);
        PatientEntity savedPatientEntity = patientService.save(patientEntity);
        return new ResponseEntity<>(
                patientMapper.mapToDTO(savedPatientEntity), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientDTO> partialUpdatePatient(@PathVariable("id") Long id, @RequestBody @Valid PatientDTO patientDTO) {
        if (!patientService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PatientEntity patientEntity = patientMapper.mapFrom(patientDTO);
        PatientEntity savedPatientEntity = patientService.partialUpdate(id, patientEntity);
        return new ResponseEntity<>(
                patientMapper.mapToDTO(savedPatientEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePatient(@PathVariable("id") Long id) {
        if (!patientService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        patientService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

