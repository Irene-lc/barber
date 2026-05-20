package edu.upb.barber.repository.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistorialCambioRequest {
    private String agendaEventoId;
    private String modificadoPorUsuarioId;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
}
