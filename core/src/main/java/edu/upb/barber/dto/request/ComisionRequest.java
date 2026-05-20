package edu.upb.barber.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class ComisionRequest {
    private String ventaDetalleId;
    private String empleadoId;
    private BigDecimal porcentajeAplicado;
    private BigDecimal montoComision;
    private boolean liquidada = false;
    private OffsetDateTime liquidadaEn;
}
