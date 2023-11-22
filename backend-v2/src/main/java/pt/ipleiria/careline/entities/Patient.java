package pt.ipleiria.careline.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import pt.ipleiria.careline.enums.Sex;

import java.util.Date;

@Entity
public class Patient {
    @Id
    @SequenceGenerator(name = "patient_id_sequence", sequenceName = "patient_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_id_sequence")
    private Integer id;
    @NotNull
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @NotNull
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Sex sex;
    @NotNull
    private String address;
    @NotNull
    private String phone;
    @NotNull
    private String nif;
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @NotNull
    private String nus;
    @NotNull
    @CreatedDate
    private Date created_at;
    @NotNull
    private boolean active;
    @Version
    private int version;

    public Patient() {
    }

    public Patient(String name, String email, String password, Sex sex, String address, String phone, String nif, Date birthDate, String nus, Date created_at, boolean active, int version) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.sex = sex;
        this.address = address;
        this.phone = phone;
        this.nif = nif;
        this.birthDate = birthDate;
        this.nus = nus;
        this.created_at = created_at;
        this.active = active;
        this.version = version;
    }

    public Patient(Integer id, String name, String email, String password, Sex sex, String address, String phone, String nif, Date birthDate, String nus, Date created_at, boolean active, int version) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.sex = sex;
        this.address = address;
        this.phone = phone;
        this.nif = nif;
        this.birthDate = birthDate;
        this.nus = nus;
        this.created_at = created_at;
        this.active = active;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getNus() {
        return nus;
    }

    public void setNus(String nus) {
        this.nus = nus;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
