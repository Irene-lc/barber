package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.HistorialCambio;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class HistorialCambioResponse {
    private String id;
    private String agendaEventoId;
    private String modificadoPorUsuarioId;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
    private OffsetDateTime modificadoEn;

    public static HistorialCambioResponse fromEntity(HistorialCambio h) {
        HistorialCambioResponse dto = new HistorialCambioResponse();
        dto.id = h.getId();
        if (h.getAgendaEvento() != null) {
            dto.agendaEventoId = h.getAgendaEvento().getId();
        }
        if (h.getModificadoPorUsuario() != null) {
            dto.modificadoPorUsuarioId = h.getModificadoPorUsuario().getId();
        }
        dto.campoModificado = h.getCampoModificado();
        dto.valorAnterior = h.getValorAnterior();
        dto.valorNuevo = h.getValorNuevo();
        dto.modificadoEn = h.getModificadoEn();
        return dto;
    }
}
