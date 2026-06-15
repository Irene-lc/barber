package edu.upb.barber.repository.entity;

import edu.upb.barber.repository.entity.enums.MetodoPago;
import edu.upb.barber.repository.entity.enums.TipoEmpresa;
import jakarta.persistence.*;
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
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoEmpresa tipoEmpresa;

    @PrePersist
    @PreUpdate
    private void syncActiveFlags() {
        if (activo == null) {
            activo = Boolean.TRUE;
        }
    }

}
