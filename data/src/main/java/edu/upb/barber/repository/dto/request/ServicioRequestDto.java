package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ServicioRequestDto {

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer duracion;

    @JsonProperty("empresa_id")
    private String empresaId;

    private String destinatario; // Mapeado a TipoDestinatarioServicio
    private String categoria;    // Mapeado a CategoriaServicio
    private Boolean activo = true;
}
