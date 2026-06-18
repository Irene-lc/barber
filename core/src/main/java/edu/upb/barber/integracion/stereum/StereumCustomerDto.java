package edu.upb.barber.integracion.stereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//datos del cliente
@Data
public class StereumCustomerDto {
    private String name;
    private String lastname;
    @JsonProperty("document_number")
    private String documentNumber;
}
