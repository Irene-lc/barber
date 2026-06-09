package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.AgendaEventoEmpleado;
import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AgendaEventoEmpleadoResponseDto {

    private String id;

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("empleado_nombre")
    private String empleadoNombre;

    @JsonProperty("rol_en_evento")
    private RolEmpleadoEvento rolEnEvento;

    public AgendaEventoEmpleadoResponseDto(AgendaEventoEmpleado empleadoEvento) {
        this.id = empleadoEvento.getId();
        if (empleadoEvento.getEmpleado() != null) {
            this.empleadoId = empleadoEvento.getEmpleado().getId();
            this.empleadoNombre = empleadoEvento.getEmpleado().getNombre();
        }
        this.rolEnEvento = empleadoEvento.getRolEnEvento();
    }
}
