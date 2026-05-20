package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.AgendaEventoEmpleado;
import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgendaEventoEmpleadoResponse {
    private String id;
    private String agendaEventoId;
    private String empleadoId;
    private String empleadoNombre;
    private RolEmpleadoEvento rolEnEvento;

    public static AgendaEventoEmpleadoResponse fromEntity(AgendaEventoEmpleado a) {
        AgendaEventoEmpleadoResponse dto = new AgendaEventoEmpleadoResponse();
        dto.id = a.getId();
        if (a.getAgendaEvento() != null) {
            dto.agendaEventoId = a.getAgendaEvento().getId();
        }
        if (a.getEmpleado() != null) {
            dto.empleadoId = a.getEmpleado().getId();
            dto.empleadoNombre = a.getEmpleado().getNombre();
        }
        dto.rolEnEvento = a.getRolEnEvento();
        return dto;
    }
}
