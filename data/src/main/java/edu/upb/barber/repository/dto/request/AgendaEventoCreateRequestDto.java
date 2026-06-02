package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class AgendaEventoCreateRequestDto {

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("tipo_evento")
    private TipoEvento tipoEvento;

    private OffsetDateTime inicio;
    private OffsetDateTime fin;
    private String notas;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("mascota_id")
    private String mascotaId;

    private String motivo;

    private List<AgendaEventoDetalleCreateDto> detalles;
    private List<AgendaEventoEmpleadoCreateDto> empleados;
}
