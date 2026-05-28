package edu.upb.barber.repository.dto.request;


import lombok.Data;

@Data
public class ProductoRequestDto {

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}