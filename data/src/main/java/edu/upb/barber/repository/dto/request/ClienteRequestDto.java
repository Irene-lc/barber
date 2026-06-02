package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClienteRequestDto {

    @JsonProperty("empresa_id")
    private String empresaId;

    private String nombre;
    private String telefono;
    private String email;
    private String documento;
    private String notas;
    private boolean activo = true;
}