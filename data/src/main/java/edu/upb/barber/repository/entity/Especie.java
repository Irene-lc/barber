package edu.upb.barber.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(
        name = "especie",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_especie_nombre", columnNames = "nombre")
        }
)
public class Especie extends AuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}