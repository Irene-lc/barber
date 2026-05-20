package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.PermisoAgenda;
import edu.upb.barber.repository.entity.enums.TipoPermiso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PermisoAgendaResponse {
    private String id;
    private String empleadoId;
    private String empleadoNombre;
    private String sucursalId;
    private String sucursalNombre;
    private String otorgadoPorUsuarioId;
    private TipoPermiso tipoPermiso;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivo;
    private boolean activo;

    public static PermisoAgendaResponse fromEntity(PermisoAgenda p) {
        PermisoAgendaResponse dto = new PermisoAgendaResponse();
        dto.id = p.getId();
        if (p.getEmpleado() != null) {
            dto.empleadoId = p.getEmpleado().getId();
            dto.empleadoNombre = p.getEmpleado().getNombre();
        }
        if (p.getSucursal() != null) {
            dto.sucursalId = p.getSucursal().getId();
            dto.sucursalNombre = p.getSucursal().getNombre();
        }
        if (p.getOtorgadoPorUsuario() != null) {
            dto.otorgadoPorUsuarioId = p.getOtorgadoPorUsuario().getId();
        }
        dto.tipoPermiso = p.getTipoPermiso();
        dto.fechaDesde = p.getFechaDesde();
        dto.fechaHasta = p.getFechaHasta();
        dto.horaInicio = p.getHoraInicio();
        dto.horaFin = p.getHoraFin();
        dto.motivo = p.getMotivo();
        dto.activo = p.isActivo();
        return dto;
    }
}
