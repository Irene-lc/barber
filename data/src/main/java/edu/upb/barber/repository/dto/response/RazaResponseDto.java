package edu.upb.barber.repository.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RazaResponseDto {

    private String id;

    private String nombre;

    private String especieId;

    private Boolean activo;
}