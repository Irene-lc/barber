package edu.upb.barber.repository.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MascotaResponseDto {

    private String id;

    private String nombre;

    private Integer edad;

    @com.fasterxml.jackson.annotation.JsonProperty("cliente_id")
    private String clienteId;

    @com.fasterxml.jackson.annotation.JsonProperty("raza_id")
    private String razaId;

    private Boolean activo;
}