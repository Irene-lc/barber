package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaRequestDto {

    private String nombre;

    @JsonProperty("razon_social")
    private String razonSocial;

    private String nit;

    private String telefono;

    private String email;

    private Boolean activo;
}
