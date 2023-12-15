package pt.ipleiria.careline.domain.dto.data;

import pt.ipleiria.careline.domain.dto.PatientDTO;
import pt.ipleiria.careline.domain.dto.responses.PatientResponseDTO;

import java.time.Instant;

public class TemperatureDTO extends DataDTO {
    private Float temperature;

    public TemperatureDTO() {
    }

    public TemperatureDTO(PatientResponseDTO patient, Instant createdAt, Float temperature) {
        super(patient, createdAt);
        this.temperature = temperature;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

}
