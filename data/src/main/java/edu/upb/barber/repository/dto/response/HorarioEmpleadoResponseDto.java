package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class HorarioEmpleadoResponseDto {

    private String id;

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("empleado_nombre")
    private String empleadoNombre;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    @JsonProperty("dia_semana")
    private DiaSemana diaSemana;

    @JsonProperty("hora_inicio")
    private LocalTime horaInicio;

    @JsonProperty("hora_fin")
    private LocalTime horaFin;

    private Boolean activo;

    public HorarioEmpleadoResponseDto(HorarioEmpleado horario) {
        this.id = horario.getId();
        if (horario.getEmpleado() != null) {
            this.empleadoId = horario.getEmpleado().getId();
            this.empleadoNombre = horario.getEmpleado().getNombre();
        }
        if (horario.getSucursal() != null) {
            this.sucursalId = horario.getSucursal().getId();
            this.sucursalNombre = horario.getSucursal().getNombre();
        }
        this.diaSemana = horario.getDiaSemana();
        this.horaInicio = horario.getHoraInicio();
        this.horaFin = horario.getHoraFin();
        this.activo = horario.isActivo();
    }
}
