package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import lombok.Data;

@Data
public class AgendaEventoCreateResponseDto {

    @JsonProperty("agenda_evento_id")
    private String agendaEventoId;

    private EstadoEvento estado;
}
