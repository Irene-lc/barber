package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpleadoDisponibleResponseDto {

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("empleado_nombre")
    private String empleadoNombre;

    private String cargo;
    private String especialidad;

    @JsonProperty("foto_url")
    private String fotoUrl;
}
