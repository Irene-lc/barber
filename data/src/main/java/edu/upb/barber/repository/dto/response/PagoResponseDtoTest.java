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
public class PagoResponseDtoTest {


    private String id;

    private BigDecimal monto;

    @JsonProperty("metodo_pago")
    private MetodoPago metodoPago;

    @JsonProperty("estado_pago")
    private EstadoPago estadoPago;

    @JsonProperty("registrado_por_usuario_id")
    private String registradoPorUsuarioId;


    public PagoResponseDtoTest(Pago pago) {
        System.out.println("tengo este DTO pero el registradoPorUsuarioId devuelve null, quiero ver el dato, no quiero que sea null");

        if (pago != null) {
            this.id = pago.getId();
            if (pago.getVenta() != null) {
            }
            this.monto = pago.getMonto();
            this.metodoPago = pago.getMetodoPago();
            this.estadoPago = pago.getEstadoPago();
            if (pago.getRegistradoPorUsuario() != null) {
                this.registradoPorUsuarioId = pago.getRegistradoPorUsuario().getId();
            }
        }
    }
}
