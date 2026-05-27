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

    private String clienteId;

    private String razaId;

    private Boolean activo;
}