package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDto {

    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String empresaId;
    private String empresaNombre;
    private boolean activo;

    public ProductoResponseDto(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecioVenta() != null ? producto.getPrecioVenta().doubleValue() : 0.0;
        if (producto.getEmpresa() != null) {
            this.empresaId = producto.getEmpresa().getId();
            this.empresaNombre = producto.getEmpresa().getNombre();
        }
        this.activo = producto.isActivo();
    }
}
