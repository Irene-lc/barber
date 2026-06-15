package edu.upb.barber.service.integracion.stereum;

import lombok.Data;
//este solo verifica el pago, no devuelve mas datos, solo el estado del pago
@Data
public class StereumVerifyResponseDto {
    private String status;
}
