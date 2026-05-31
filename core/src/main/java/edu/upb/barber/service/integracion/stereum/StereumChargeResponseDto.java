package edu.upb.barber.service.integracion.stereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StereumChargeResponseDto {
    private String id;
    
    @JsonProperty("qr_base64")
    private String qrBase64;
    
    @JsonProperty("payment_link")
    private String paymentLink;
}
