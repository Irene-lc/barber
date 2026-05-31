package edu.upb.barber.repository.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SucursalResponseDto {

    private String id;
    private String nombre;
    private String direccion;
    private String telefono;
}
