package edu.upb.barber.integracion.stereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StereumChargeRequestDto {
    private String country;
    private String amount;
    private String currency;
    private String network;
    
    @JsonProperty("idempotency_key")
    private String idempotencyKey; //clave unica para evitar cargos duplicados
    
    @JsonProperty("charge_reason")
    private String chargeReason;
    
    @JsonProperty("reservation_validity_time")
    private String reservationValidityTime; //tiempo del qr
    
    private StereumCustomerDto customer;
}
