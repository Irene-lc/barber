package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.HorarioEmpleadoFecha;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class HorarioEmpleadoFechaResponseDto {
    private String id;

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("empleado_nombre")
    private String empleadoNombre;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    private LocalDate fecha;

    @JsonProperty("hora_inicio")
    private LocalTime horaInicio;

    @JsonProperty("hora_fin")
    private LocalTime horaFin;

    private Boolean activo;

    public HorarioEmpleadoFechaResponseDto(HorarioEmpleadoFecha horario) {
        this.id = horario.getId();
        if (horario.getEmpleado() != null) {
            this.empleadoId = horario.getEmpleado().getId();
            this.empleadoNombre = horario.getEmpleado().getNombre();
        }
        if (horario.getSucursal() != null) {
            this.sucursalId = horario.getSucursal().getId();
            this.sucursalNombre = horario.getSucursal().getNombre();
        }
        this.fecha = horario.getFecha();
        this.horaInicio = horario.getHoraInicio();
        this.horaFin = horario.getHoraFin();
        this.activo = horario.isActivo();
    }
}
