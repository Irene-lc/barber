package edu.upb.springsito.repository.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "agenda_evento_detalle")
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
    @JoinColumn(name = "combo_id")
    private ComboServicio combo;

    @Column(name = "duracion_estimada_min", nullable = false)
    private Integer duracionEstimadaMin;

    @Column(name = "precio_acordado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAcordado;

    public String getId() { return id; }
    public AgendaEvento getAgendaEvento() { return agendaEvento; }
    public void setAgendaEvento(AgendaEvento agendaEvento) { this.agendaEvento = agendaEvento; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public ComboServicio getCombo() { return combo; }
    public void setCombo(ComboServicio combo) { this.combo = combo; }
    public Integer getDuracionEstimadaMin() { return duracionEstimadaMin; }
    public void setDuracionEstimadaMin(Integer duracionEstimadaMin) { this.duracionEstimadaMin = duracionEstimadaMin; }
    public BigDecimal getPrecioAcordado() { return precioAcordado; }
    public void setPrecioAcordado(BigDecimal precioAcordado) { this.precioAcordado = precioAcordado; }
}
