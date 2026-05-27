package edu.upb.barber.repository.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductoResponseDto {

    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}
