package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransaccionRequestDto {

    private String country;
    private BigDecimal amount;

    @JsonProperty("status_description")
    private String statusDescription;

    @JsonProperty("on_main_net")
    private boolean onMainNet;

    @JsonProperty("amount_received")
    private BigDecimal amountReceived;

    private String fee;

    @JsonProperty("idempotency_key")
    private UUID idempotencyKey;

    private String currency;
    private UUID id;

    @JsonProperty("created_date")
    private long createdDate;

    private String network;
    private String status;
}