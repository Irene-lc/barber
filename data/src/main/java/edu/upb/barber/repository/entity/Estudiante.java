package edu.upb.barber.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "estudiantes")
public class Estudiante extends AuditableEntity{
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "apellido", length = 120)
    private String apellido;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", referencedColumnName = "id")
    private Materia materia;

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @JsonProperty("nro_telefono")
    @Column(name = "nro_telefono")
    private String nroTelefono;

    @JsonProperty("nro_documento")
    @Column(name = "nro_documento")
    private String nroDocumento;


}
