package edu.upb.barber.repository.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "historial_cambio")
public class HistorialCambio {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_evento_id", nullable = false)
    private AgendaEvento agendaEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modificado_por_usuario_id")
    private Usuario modificadoPorUsuario;

    @Column(name = "campo_modificado", nullable = false, length = 100)
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "modificado_en", nullable = false, updatable = false)
    private OffsetDateTime modificadoEn;

    @PrePersist
    protected void onCreate() {
        this.modificadoEn = OffsetDateTime.now();
    }

    public String getId() { return id; }
    public AgendaEvento getAgendaEvento() { return agendaEvento; }
    public void setAgendaEvento(AgendaEvento agendaEvento) { this.agendaEvento = agendaEvento; }
    public Usuario getModificadoPorUsuario() { return modificadoPorUsuario; }
    public void setModificadoPorUsuario(Usuario modificadoPorUsuario) { this.modificadoPorUsuario = modificadoPorUsuario; }
    public String getCampoModificado() { return campoModificado; }
    public void setCampoModificado(String campoModificado) { this.campoModificado = campoModificado; }
    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }
    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }
    public OffsetDateTime getModificadoEn() { return modificadoEn; }
}
