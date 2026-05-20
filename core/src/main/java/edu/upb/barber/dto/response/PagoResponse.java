package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class PagoResponse {
    private String id;
    private String ventaId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
    private OffsetDateTime pagadoEn;
    private String registradoPorUsuarioId;

    public static PagoResponse fromEntity(Pago p) {
        PagoResponse dto = new PagoResponse();
        dto.id = p.getId();
        if (p.getVenta() != null) {
            dto.ventaId = p.getVenta().getId();
        }
        dto.monto = p.getMonto();
        dto.metodoPago = p.getMetodoPago();
        dto.estadoPago = p.getEstadoPago();
        dto.pagadoEn = p.getPagadoEn();
        if (p.getRegistradoPorUsuario() != null) {
            dto.registradoPorUsuarioId = p.getRegistradoPorUsuario().getId();
        }
        return dto;
    }
}
