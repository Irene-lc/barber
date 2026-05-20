package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.AgendaEventoDetalle;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgendaEventoDetalleResponse {
    private String id;
    private String agendaEventoId;
    private String servicioId;
    private String servicioNombre;
    private String comboServicioId;
    private String comboServicioNombre;
    private int duracionEstimadaMinutos;
    private BigDecimal precioAcordado;
    private String notas;

    public static AgendaEventoDetalleResponse fromEntity(AgendaEventoDetalle a) {
        AgendaEventoDetalleResponse dto = new AgendaEventoDetalleResponse();
        dto.id = a.getId();
        if (a.getAgendaEvento() != null) {
            dto.agendaEventoId = a.getAgendaEvento().getId();
        }
        if (a.getServicio() != null) {
            dto.servicioId = a.getServicio().getId();
            dto.servicioNombre = a.getServicio().getNombre();
        }
        if (a.getComboServicio() != null) {
            dto.comboServicioId = a.getComboServicio().getId();
            dto.comboServicioNombre = a.getComboServicio().getNombre();
        }
        dto.duracionEstimadaMinutos = a.getDuracionEstimadaMinutos();
        dto.precioAcordado = a.getPrecioAcordado();
        dto.notas = a.getNotas();
        return dto;
    }
}
