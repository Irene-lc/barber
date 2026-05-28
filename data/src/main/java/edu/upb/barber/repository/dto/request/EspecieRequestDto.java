package edu.upb.barber.repository.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EspecieRequestDto {

    private String nombre;

    private Boolean activo;
}