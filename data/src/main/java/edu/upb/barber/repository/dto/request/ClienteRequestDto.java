package edu.upb.barber.repository.dto.request;

import lombok.Data;

@Data
public class ClienteRequestDto {

    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
}