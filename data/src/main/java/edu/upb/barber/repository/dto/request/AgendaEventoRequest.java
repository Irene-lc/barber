package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AgendaEventoRequest {
    private String clienteId;
    private String mascotaId;
    private String sucursalId;
    private String creadoPorUsuarioId;
    @JsonProperty("tipo_evento")
    private TipoEvento tipoEvento = TipoEvento.CITA;
    private EstadoEvento estado = EstadoEvento.PENDIENTE;
    private OffsetDateTime inicio;
    private OffsetDateTime fin;
    private String notas;
}
