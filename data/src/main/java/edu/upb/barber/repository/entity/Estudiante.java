package edu.upb.barber.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 150)
    private String apellido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @Column(name = "nota", nullable = false)
    private int nota;

    @Column(name = "nro_telefono", length = 30)
    private String nroTelefono;

    @Column(name = "nro_documento", length = 120)
    private String nroDocumento;

}
