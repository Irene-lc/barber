package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class PagoRequestDto {

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
}
