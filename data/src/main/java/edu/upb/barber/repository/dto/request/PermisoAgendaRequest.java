package edu.upb.barber.repository.dto.request;

import edu.upb.barber.repository.entity.enums.TipoPermiso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PermisoAgendaRequest {
    private String empleadoId;
    private String sucursalId;
    private String otorgadoPorUsuarioId;
    private TipoPermiso tipoPermiso;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivo;
    private boolean activo = true;
}
