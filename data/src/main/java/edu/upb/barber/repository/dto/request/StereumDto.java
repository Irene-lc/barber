package edu.upb.barber.repository.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.dto.response.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StereumDto {

    private String country;
    private String amount;
    private String currency;
    private String network;
    @JsonProperty("idempotency_key")
    private String idempotencyKey;
    @JsonProperty("charge_reason")
    private String chargeReason;
    @JsonProperty("reservation_validity_time")
    private String reservationValidityTime;
    private Customer customer;

}
