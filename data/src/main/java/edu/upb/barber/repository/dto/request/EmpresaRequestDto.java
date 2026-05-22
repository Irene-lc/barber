package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaRequestDto {
    private String nombre;
    private boolean activa;
    private String nit;
    @JsonProperty("razon_social")
    private String razon_social;



}
