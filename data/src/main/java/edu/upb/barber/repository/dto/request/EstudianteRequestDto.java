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
public class EstudianteRequestDto {

    private String nombre;

    private String apellido;

    @JsonProperty("materia_id")
    private String materiaId;

    private int nota;

    @JsonProperty("nro_telefono")
    private String nroTelefono;

    @JsonProperty("nro_documento")
    private String nroDocumento;

}
