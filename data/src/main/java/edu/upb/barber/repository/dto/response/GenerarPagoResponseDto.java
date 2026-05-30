package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerarPagoResponseDto {

    @JsonProperty("pago_id")
    private String pagoId;

    @JsonProperty("qr_base64")
    private String qrBase64;

    @JsonProperty("payment_link")
    private String paymentLink;

}
