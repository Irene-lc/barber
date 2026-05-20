package edu.upb.springsito.dto;

import edu.upb.springsito.repository.entity.enums.EstadoEvento;
import edu.upb.springsito.repository.entity.enums.TipoEvento;

public record AgendaEventoDto(
        String clienteNombre,
        TipoEvento tipoEvento,
        EstadoEvento estado) {
}
