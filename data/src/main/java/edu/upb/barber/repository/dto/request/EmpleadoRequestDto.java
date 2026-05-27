package edu.upb.barber.repository.dto.request;

import lombok.Data;

@Data
public class EmpleadoRequestDto {

    private String nombre;
    private String apellido;
    private String cargo;
    private Double salario;
}
