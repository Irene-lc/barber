package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerarPagoRequestDto {

    @JsonProperty("venta_id")
    private String ventaId;

}
