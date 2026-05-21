package edu.upb.barber.repository.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "empleado",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_empleado_usuario", columnNames = "usuario_id")
    }
)
@Getter
@Setter
public class Empleado extends AuditableEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "cargo", length = 80)
    private String cargo;

    @Column(name = "especialidad", length = 120)
    private String especialidad;

    @Column(name = "porcentaje_comision", precision = 5, scale = 2)
    private BigDecimal porcentajeComision;

    @Column(name = "pago_fijo_mensual", precision = 12, scale = 2)
    private BigDecimal pagoFijoMensual;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

}
