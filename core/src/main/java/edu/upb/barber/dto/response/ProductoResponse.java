package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Producto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductoResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private boolean activo;

    public static ProductoResponse fromEntity(Producto p) {
        ProductoResponse dto = new ProductoResponse();
        dto.id = p.getId();
        if (p.getEmpresa() != null) {
            dto.empresaId = p.getEmpresa().getId();
            dto.empresaNombre = p.getEmpresa().getNombre();
        }
        dto.nombre = p.getNombre();
        dto.descripcion = p.getDescripcion();
        dto.precioVenta = p.getPrecioVenta();
        dto.activo = p.isActivo();
        return dto;
    }
}
