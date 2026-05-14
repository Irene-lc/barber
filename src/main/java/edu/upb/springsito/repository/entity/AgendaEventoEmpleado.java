package edu.upb.springsito.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "agenda_evento_empleado")
public class AgendaEventoEmpleado extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_evento_id", nullable = false)
    private AgendaEvento agendaEvento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "rol_en_evento", nullable = false, length = 80)
    private String rolEnEvento;

    public String getId() { return id; }
    public AgendaEvento getAgendaEvento() { return agendaEvento; }
    public void setAgendaEvento(AgendaEvento agendaEvento) { this.agendaEvento = agendaEvento; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public String getRolEnEvento() { return rolEnEvento; }
    public void setRolEnEvento(String rolEnEvento) { this.rolEnEvento = rolEnEvento; }
}
