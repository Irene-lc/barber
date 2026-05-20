package edu.upb.barber.repository.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "agenda_evento_detalle")
@Getter
@Setter
public class AgendaEventoDetalle extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_evento_id", nullable = false)
    private AgendaEvento agendaEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_servicio_id")
    private ComboServicio comboServicio;

    @Column(name = "duracion_estimada_minutos", nullable = false)
    private int duracionEstimadaMinutos;

    @Column(name = "precio_acordado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAcordado;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

}
