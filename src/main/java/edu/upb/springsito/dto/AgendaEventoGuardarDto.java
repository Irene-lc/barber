package edu.upb.springsito.dto;

import java.time.OffsetDateTime;

import edu.upb.springsito.repository.entity.enums.EstadoEvento;
import edu.upb.springsito.repository.entity.enums.TipoEvento;

public record AgendaEventoGuardarDto(
        String clienteId,
        String sucursalId,
        TipoEvento tipoEvento,
        EstadoEvento estado,
        OffsetDateTime inicio,
        OffsetDateTime fin,
        String notas) {
}
