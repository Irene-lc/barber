package edu.upb.barber.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "empresa")
public class Empresa extends AuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "nit", nullable = false, unique = true, length = 30)
    private String nit;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "activa", nullable = false)
    private Boolean activa = true;

    @PrePersist
    @PreUpdate
    private void syncActiveFlags() {
        if (activa == null) {
            activa = Boolean.TRUE;
        }
    }

}
