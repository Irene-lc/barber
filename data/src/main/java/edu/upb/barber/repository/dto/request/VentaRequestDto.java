package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequestDto {

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("agenda_evento_id")
    private String agendaEventoId;

    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private String notas;

    private List<VentaDetalleRequestDto> detalles;
}
