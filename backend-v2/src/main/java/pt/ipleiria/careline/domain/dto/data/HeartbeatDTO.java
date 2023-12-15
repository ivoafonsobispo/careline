package pt.ipleiria.careline.domain.dto.data;

import pt.ipleiria.careline.domain.dto.PatientDTO;
import pt.ipleiria.careline.domain.dto.responses.PatientResponseDTO;

import java.time.Instant;

public class HeartbeatDTO extends DataDTO {
    private Integer heartbeat;


    public HeartbeatDTO() {
    }

    public HeartbeatDTO(PatientResponseDTO patient, Instant createdAt, Integer heartbeat) {
        super(patient, createdAt);
        this.heartbeat = heartbeat;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

}
