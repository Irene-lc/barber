package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.VentaDetalle;
import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class VentaDetalleResponseDto {

    private String id;

    @JsonProperty("tipo_item")
    private TipoItemVenta tipoItem;

    @JsonProperty("servicio_id")
    private String servicioId;

    @JsonProperty("servicio_nombre")
    private String servicioNombre;

    @JsonProperty("producto_id")
    private String productoId;

    @JsonProperty("producto_nombre")
    private String productoNombre;

    @JsonProperty("combo_servicio_id")
    private String comboServicioId;

    @JsonProperty("combo_servicio_nombre")
    private String comboServicioNombre;

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("empleado_nombre")
    private String empleadoNombre;

    private int cantidad;

    @JsonProperty("precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal descuento;
    private BigDecimal subtotal;
    private String notas;

    public VentaDetalleResponseDto(VentaDetalle detalle) {
        this.id = detalle.getId();
        this.tipoItem = detalle.getTipoItem();
        if (detalle.getServicio() != null) {
            this.servicioId = detalle.getServicio().getId();
            this.servicioNombre = detalle.getServicio().getNombre();
        }
        if (detalle.getProducto() != null) {
            this.productoId = detalle.getProducto().getId();
            this.productoNombre = detalle.getProducto().getNombre();
        }
        if (detalle.getComboServicio() != null) {
            this.comboServicioId = detalle.getComboServicio().getId();
            this.comboServicioNombre = detalle.getComboServicio().getNombre();
        }
        if (detalle.getEmpleado() != null) {
            this.empleadoId = detalle.getEmpleado().getId();
            this.empleadoNombre = detalle.getEmpleado().getNombre();
        }
        this.cantidad = detalle.getCantidad();
        this.precioUnitario = detalle.getPrecioUnitario();
        this.descuento = detalle.getDescuento();
        this.subtotal = detalle.getSubtotal();
        this.notas = detalle.getNotas();
    }
}
