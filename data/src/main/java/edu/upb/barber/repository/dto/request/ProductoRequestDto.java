package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductoRequestDto {

    private String nombre;
    private String descripcion;
    private Double precio;

    @JsonProperty("imagen_url")
    private String imagenUrl;

    @JsonProperty("empresa_id")
    private String empresaId;

    private Boolean activo = true;
}
