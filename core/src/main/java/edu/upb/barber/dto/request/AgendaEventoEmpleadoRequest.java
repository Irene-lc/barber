package edu.upb.barber.dto.request;

import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgendaEventoEmpleadoRequest {
    private String agendaEventoId;
    private String empleadoId;
    private RolEmpleadoEvento rolEnEvento = RolEmpleadoEvento.RESPONSABLE;
}
