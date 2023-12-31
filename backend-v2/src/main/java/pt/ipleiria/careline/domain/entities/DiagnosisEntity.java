package pt.ipleiria.careline.domain.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import pt.ipleiria.careline.domain.entities.embed.Medication;
import pt.ipleiria.careline.domain.entities.users.PatientEntity;
import pt.ipleiria.careline.domain.entities.users.ProfessionalEntity;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "diagnosis")
public class DiagnosisEntity {
    @CurrentTimestamp
    @Column(name = "created_at")
    public Instant createdAt;
    @ElementCollection
    @CollectionTable(name = "medications", joinColumns = @JoinColumn(name = "diagnosis_id"))
    List<Medication> medications;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "diagnosis_id_seq")
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "professional_id")
    private ProfessionalEntity professional;
    @NotNull(message = "Diagnosis is required")
    private String diagnosis;

}
