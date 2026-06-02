package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.AgendaEvento;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class AgendaEventoResponseDto {

    private String id;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("cliente_nombre")
    private String clienteNombre;

    @JsonProperty("mascota_id")
    private String mascotaId;

    @JsonProperty("mascota_nombre")
    private String mascotaNombre;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    @JsonProperty("tipo_evento")
    private TipoEvento tipoEvento;

    private EstadoEvento estado;
    private OffsetDateTime inicio;
    private OffsetDateTime fin;
    private String notas;

    public AgendaEventoResponseDto(AgendaEvento ae) {
        this.id = ae.getId();
        if (ae.getCliente() != null) {
            this.clienteId = ae.getCliente().getId();
            this.clienteNombre = ae.getCliente().getNombre();
        }
        if (ae.getMascota() != null) {
            this.mascotaId = ae.getMascota().getId();
            this.mascotaNombre = ae.getMascota().getNombre();
        }
        if (ae.getSucursal() != null) {
            this.sucursalId = ae.getSucursal().getId();
            this.sucursalNombre = ae.getSucursal().getNombre();
        }
        this.tipoEvento = ae.getTipoEvento();
        this.estado = ae.getEstado();
        this.inicio = ae.getInicio();
        this.fin = ae.getFin();
        this.notas = ae.getNotas();
    }
}
