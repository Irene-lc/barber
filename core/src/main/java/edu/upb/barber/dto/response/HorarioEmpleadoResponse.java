package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class HorarioEmpleadoResponse {
    private String id;
    private String empleadoId;
    private String empleadoNombre;
    private String sucursalId;
    private String sucursalNombre;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean activo;

    public static HorarioEmpleadoResponse fromEntity(HorarioEmpleado h) {
        HorarioEmpleadoResponse dto = new HorarioEmpleadoResponse();
        dto.id = h.getId();
        if (h.getEmpleado() != null) {
            dto.empleadoId = h.getEmpleado().getId();
            dto.empleadoNombre = h.getEmpleado().getNombre();
        }
        if (h.getSucursal() != null) {
            dto.sucursalId = h.getSucursal().getId();
            dto.sucursalNombre = h.getSucursal().getNombre();
        }
        dto.diaSemana = h.getDiaSemana();
        dto.horaInicio = h.getHoraInicio();
        dto.horaFin = h.getHoraFin();
        dto.activo = h.isActivo();
        return dto;
    }
}
