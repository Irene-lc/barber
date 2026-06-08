package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InventarioSucursalRequestDto {

    @JsonProperty("producto_id")
    private String productoId;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("stock_actual")
    private Integer stockActual;

    @JsonProperty("stock_minimo")
    private Integer stockMinimo;

    private Boolean activo;
}

