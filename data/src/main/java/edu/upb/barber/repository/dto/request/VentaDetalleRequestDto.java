package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class VentaDetalleRequestDto {

    @JsonProperty("tipo_item")
    private TipoItemVenta tipoItem;

    @JsonProperty("servicio_id")
    private String servicioId;

    @JsonProperty("producto_id")
    private String productoId;

    @JsonProperty("combo_servicio_id")
    private String comboServicioId;

    @JsonProperty("empleado_id")
    private String empleadoId;

    private int cantidad = 1;

    @JsonProperty("precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal descuento = BigDecimal.ZERO;

    private String notas;
}
