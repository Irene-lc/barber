package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SucursalRequestDto {

    private String nombre;
    private String direccion;
    private String telefono;

    @JsonProperty("empresa_id")
    private String empresaId;

    private Boolean activo = true;
}
