package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactonRequestDto {

    private String country;
    private BigDecimal amount;
    @JsonProperty("status_description")
    private String statusDescription;
    @JsonProperty("on_main_net")
    private Boolean onMainNet;
    @JsonProperty("amount_received")
    private BigDecimal amountReceived;
    private BigDecimal fee;
    @JsonProperty("idempotency_key")
    private String idempotencyKey;
    private String currency;
    private String id;
    @JsonProperty("created_date")
    private Long createdDate;
    private String network;
    private String status;

}