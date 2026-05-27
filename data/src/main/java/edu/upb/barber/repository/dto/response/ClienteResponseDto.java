package edu.upb.barber.repository.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponseDto {

    private String id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
}