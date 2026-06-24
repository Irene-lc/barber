package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WalkInRequestDto(
    @JsonProperty("clienteId") String clienteId,
    @JsonProperty("empleadoId") String empleadoId,
    @JsonProperty("servicioIds") List<String> servicioIds,
    @JsonProperty("productoIds") List<String> productoIds,
    @JsonProperty("productos") List<ProductoCantidadDto> productos
) {
    public record ProductoCantidadDto(
        @JsonProperty("producto_id") String productoId,
        @JsonProperty("cantidad") Integer cantidad
    ) {}
}

