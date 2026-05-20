package edu.upb.barber.repository.dto.request;

import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class PagoRequest {
    private String ventaId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;
    private OffsetDateTime pagadoEn;
    private String registradoPorUsuarioId;
}
