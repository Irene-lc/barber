package edu.upb.barber.repository.dto.request;


import lombok.Data;

@Data
public class ServicioRequestDto {

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer duracion;
}
