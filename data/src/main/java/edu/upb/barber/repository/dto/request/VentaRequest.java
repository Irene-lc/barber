package edu.upb.barber.repository.dto.request;

import edu.upb.barber.repository.entity.enums.EstadoVenta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VentaRequest {
    private String sucursalId;
    private String clienteId;
    private String agendaEventoId;
    private String registradoPorUsuarioId;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private EstadoVenta estado = EstadoVenta.ABIERTA;
    private String notas;
}
