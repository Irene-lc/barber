package edu.upb.barber.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(
        name = "mascota",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_mascota_cliente_nombre",
                        columnNames = {"cliente_id", "nombre"}
                )
        }
)
public class Mascota extends AuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raza_id", nullable = false)
    private Raza raza;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "notas_especiales", columnDefinition = "TEXT")
    private String notasEspeciales;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
