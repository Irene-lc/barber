package edu.upb.barber.repository.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventarioSucursalRequest {
    private String productoId;
    private String sucursalId;
    private int stockActual = 0;
    private int stockMinimo = 0;
    private boolean activo = true;
}
