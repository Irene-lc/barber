package edu.upb.barber.repository.entity;

import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
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
    name = "agenda_evento_empleado",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_agenda_evento_empleado", columnNames = {"agenda_evento_id", "empleado_id"})
    }
)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_en_evento", nullable = false, length = 30)
    private RolEmpleadoEvento rolEnEvento = RolEmpleadoEvento.RESPONSABLE;

}
