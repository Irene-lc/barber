package edu.upb.barber.dto.request;

import edu.upb.barber.repository.entity.enums.DiaSemana;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class HorarioEmpleadoRequest {
    private String empleadoId;
    private String sucursalId;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean activo = true;
}
