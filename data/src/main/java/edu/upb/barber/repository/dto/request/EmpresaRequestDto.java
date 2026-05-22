package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaRequestDto {
    @JsonProperty("email_empresa")
    private Boolean activa;
    private String nit;
    private String nombre;
    private String razon_social;
}
