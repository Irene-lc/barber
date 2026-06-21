package edu.upb.barber.integracion.stereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class StereumChargeResponseDto {
    private String id; //id de la transaccion
    
    @JsonProperty("qr_base64")
    private String qrBase64;
    
    @JsonProperty("payment_link")
    private String paymentLink; //enlace para el pago 
}
