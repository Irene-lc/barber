package edu.upb.barber.service.integracion.stereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StereumCustomerDto {
    private String name;
    private String lastname;
    @JsonProperty("document_number")
    private String documentNumber;
}
