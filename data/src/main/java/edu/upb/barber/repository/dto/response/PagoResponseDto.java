package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class PagoResponseDto {

    private String id;

    @JsonProperty("venta_id")
    private String ventaId;

    private BigDecimal monto;

    @JsonProperty("metodo_pago")
    private MetodoPago metodoPago;

    @JsonProperty("estado_pago")
    private EstadoPago estadoPago;

    @JsonProperty("pagado_en")
    private OffsetDateTime pagadoEn;

    @JsonProperty("registrado_por_usuario_id")
    private String registradoPorUsuarioId;

    @JsonProperty("transaccion_externa_id")
    private String transaccionExternaId;

    public PagoResponseDto(Pago pago) {
        if (pago != null) {
            this.id = pago.getId();
            if (pago.getVenta() != null) {
                this.ventaId = pago.getVenta().getId();
            }
            this.monto = pago.getMonto();
            this.metodoPago = pago.getMetodoPago();
            this.estadoPago = pago.getEstadoPago();
            this.pagadoEn = pago.getPagadoEn();
            if (pago.getRegistradoPorUsuario() != null) {
                this.registradoPorUsuarioId = pago.getRegistradoPorUsuario().getId();
            }
            this.transaccionExternaId = pago.getTransaccionExternaId();
        }
    }
}
