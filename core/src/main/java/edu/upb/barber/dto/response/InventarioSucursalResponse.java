package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.InventarioSucursal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventarioSucursalResponse {
    private String id;
    private String productoId;
    private String productoNombre;
    private String sucursalId;
    private String sucursalNombre;
    private int stockActual;
    private int stockMinimo;
    private boolean activo;

    public static InventarioSucursalResponse fromEntity(InventarioSucursal i) {
        InventarioSucursalResponse dto = new InventarioSucursalResponse();
        dto.id = i.getId();
        if (i.getProducto() != null) {
            dto.productoId = i.getProducto().getId();
            dto.productoNombre = i.getProducto().getNombre();
        }
        if (i.getSucursal() != null) {
            dto.sucursalId = i.getSucursal().getId();
            dto.sucursalNombre = i.getSucursal().getNombre();
        }
        dto.stockActual = i.getStockActual();
        dto.stockMinimo = i.getStockMinimo();
        dto.activo = i.isActivo();
        return dto;
    }
}
