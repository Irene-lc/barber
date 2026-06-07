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
public class MascotaRequestDto {

    private String nombre;

    private Integer edad;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("raza_id")
    private String razaId;
    @JsonProperty("especie_id")
    private String especieId;

    @JsonProperty("activa")
    private Boolean activo;
}