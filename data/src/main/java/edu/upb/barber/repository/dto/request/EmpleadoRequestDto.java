package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmpleadoRequestDto {

    private String nombre;
    private String cargo;
    private String telefono;
    private String email;
    private String especialidad;

    @JsonProperty("foto_url")
    private String fotoUrl;

    @JsonProperty("empresa_id")
    private String empresaId;

    @JsonProperty("usuario_id")
    private String usuarioId;

    private Boolean disponible = true;
    private Boolean activo = true;
}
