package pt.ipleiria.careline.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ipleiria.careline.domain.dto.PatientDTO;
import pt.ipleiria.careline.domain.dto.data.TemperatureDTO;
import pt.ipleiria.careline.domain.dto.responses.TemperatureResponseDTO;
import pt.ipleiria.careline.domain.entities.data.TemperatureEntity;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.mappers.Mapper;
import pt.ipleiria.careline.services.TemperatureService;

import java.time.Instant;
import java.util.Optional;

@RequestMapping("/api/patients/{patientId}/temperatures")
@RestController
@CrossOrigin
public class TemperatureController {

    private final Mapper<TemperatureEntity, TemperatureDTO> temperatureMapper;
    private final Mapper<PatientEntity, PatientDTO> patientMapper;
    private final TemperatureService temperatureService;
    private final SimpMessagingTemplate messagingTemplate;

    public TemperatureController(Mapper<TemperatureEntity, TemperatureDTO> temperatureMapper, TemperatureService temperatureService, Mapper<PatientEntity, PatientDTO> patientMapper, SimpMessagingTemplate messagingTemplate) {
        this.temperatureMapper = temperatureMapper;
        this.patientMapper = patientMapper;
        this.temperatureService = temperatureService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<TemperatureResponseDTO> createTemperature(@PathVariable("patientId") Long patientId, @RequestBody @Valid TemperatureDTO temperatureDTO) {
        TemperatureEntity temperatureEntity = temperatureMapper.mapFrom(temperatureDTO);
        TemperatureEntity createdTemperature =
                temperatureService.create(patientId, temperatureEntity);
        TemperatureResponseDTO temperatureResponseDTO = new TemperatureResponseDTO(createdTemperature.getTemperature(), Instant.now(), createdTemperature.getSeverity());

        messagingTemplate.convertAndSend("/topic/temperatures", temperatureResponseDTO);

        return new ResponseEntity<>(temperatureResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROFESSIONAL')")
    public Page<TemperatureResponseDTO> listTemperatures(@PathVariable("patientId") Long patientId, Pageable pageable) {
        Page<TemperatureEntity> temperatureEntities = temperatureService.findAll(pageable, patientId);
        return temperatureEntities.map(temperature -> new TemperatureResponseDTO(temperature.getTemperature(), temperature.getCreatedAt(), temperature.getSeverity()));
    }

    @GetMapping("/latest")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROFESSIONAL')")
    public Page<TemperatureResponseDTO> listLatestTemperatures(@PathVariable("patientId") Long patientId, Pageable pageable) {
        Page<TemperatureEntity> temperatureEntities = temperatureService.findAllLatest(pageable, patientId);
        return temperatureEntities.map(temperature -> new TemperatureResponseDTO(temperature.getTemperature(), temperature.getCreatedAt(), temperature.getSeverity()));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROFESSIONAL')")
    public Page<TemperatureResponseDTO> listTemperaturesByDate(@PathVariable("patientId") Long patientId, @PathVariable("date") String date, Pageable pageable) {
        Page<TemperatureEntity> temperatureEntities = temperatureService.findAllByDate(pageable, patientId, date);
        return temperatureEntities.map(temperature -> new TemperatureResponseDTO(temperature.getTemperature(), temperature.getCreatedAt(), temperature.getSeverity()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROFESSIONAL')")
    public ResponseEntity<TemperatureDTO> getTemperatureById(@PathVariable("id") Long id) {
        Optional<TemperatureEntity> temperature = temperatureService.getById(id);
        return temperature.map(temperatureEntity -> {
            TemperatureDTO temperatureDTO = temperatureMapper.mapToDTO(temperatureEntity);
            return new ResponseEntity<>(temperatureDTO, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROFESSIONAL')")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        if (!temperatureService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        temperatureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
