package edu.upb.barber.repository.dto.request;

import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VentaDetalleRequest {
    private String ventaId;
    private TipoItemVenta tipoItem;
    private String servicioId;
    private String productoId;
    private String comboServicioId;
    private String empleadoId;
    private int cantidad = 1;
    private BigDecimal precioUnitario;
    private BigDecimal descuento = BigDecimal.ZERO;
    private BigDecimal subtotal;
    private String notas;
}
