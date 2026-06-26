package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDtoTest {

    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;

    public ProductoDtoTest(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecioVenta() != null ? producto.getPrecioVenta().doubleValue() : 0.0;
    }
}
