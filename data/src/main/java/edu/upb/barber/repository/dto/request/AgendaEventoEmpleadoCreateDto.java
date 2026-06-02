package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import lombok.Data;

@Data
public class AgendaEventoEmpleadoCreateDto {

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("rol_en_evento")
    private RolEmpleadoEvento rolEnEvento;
}
