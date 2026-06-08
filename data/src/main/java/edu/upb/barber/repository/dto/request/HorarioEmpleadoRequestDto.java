package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioEmpleadoRequestDto {

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("dia_semana")
    private DiaSemana diaSemana;

    @JsonProperty("hora_inicio")
    private LocalTime horaInicio;

    @JsonProperty("hora_fin")
    private LocalTime horaFin;

    private Boolean activo;
}
