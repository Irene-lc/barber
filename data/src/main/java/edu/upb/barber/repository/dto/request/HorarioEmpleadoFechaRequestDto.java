package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HorarioEmpleadoFechaRequestDto {

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    private LocalDate fecha;

    @JsonProperty("hora_inicio")
    private LocalTime horaInicio;

    @JsonProperty("hora_fin")
    private LocalTime horaFin;

    private Boolean activo;
}
