package edu.upb.barber.repository.entity;

import java.math.BigDecimal;

import edu.upb.barber.repository.entity.enums.CategoriaServicio;
import edu.upb.barber.repository.entity.enums.TipoDestinatarioServicio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@Table(
    name = "servicio",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_servicio_empresa_nombre", columnNames = {"empresa_id", "nombre"})
    }
)
public class Servicio extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "destinatario", nullable = false, length = 20)
    private TipoDestinatarioServicio destinatario;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", length = 40)
    private CategoriaServicio categoria;

    @Column(name = "duracion_minutos", nullable = false)
    private int duracionMinutos;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

}
