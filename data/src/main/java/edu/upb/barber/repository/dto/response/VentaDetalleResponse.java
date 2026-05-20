package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.VentaDetalle;
import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VentaDetalleResponse {
    private String id;
    private String ventaId;
    private TipoItemVenta tipoItem;
    private String servicioId;
    private String servicioNombre;
    private String productoId;
    private String productoNombre;
    private String comboServicioId;
    private String comboServicioNombre;
    private String empleadoId;
    private String empleadoNombre;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;
    private String notas;

    public static VentaDetalleResponse fromEntity(VentaDetalle v) {
        VentaDetalleResponse dto = new VentaDetalleResponse();
        dto.id = v.getId();
        if (v.getVenta() != null) {
            dto.ventaId = v.getVenta().getId();
        }
        dto.tipoItem = v.getTipoItem();
        if (v.getServicio() != null) {
            dto.servicioId = v.getServicio().getId();
            dto.servicioNombre = v.getServicio().getNombre();
        }
        if (v.getProducto() != null) {
            dto.productoId = v.getProducto().getId();
            dto.productoNombre = v.getProducto().getNombre();
        }
        if (v.getComboServicio() != null) {
            dto.comboServicioId = v.getComboServicio().getId();
            dto.comboServicioNombre = v.getComboServicio().getNombre();
        }
        if (v.getEmpleado() != null) {
            dto.empleadoId = v.getEmpleado().getId();
            dto.empleadoNombre = v.getEmpleado().getNombre();
        }
        dto.cantidad = v.getCantidad();
        dto.precioUnitario = v.getPrecioUnitario();
        dto.descuento = v.getDescuento();
        dto.subtotal = v.getSubtotal();
        dto.notas = v.getNotas();
        return dto;
    }
}
