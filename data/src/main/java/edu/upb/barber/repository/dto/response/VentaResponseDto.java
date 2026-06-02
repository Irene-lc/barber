package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class VentaResponseDto {

    private String id;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("cliente_nombre")
    private String clienteNombre;

    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private EstadoVenta estado;
    private String notas;
    private List<VentaDetalleResponseDto> detalles;

    public VentaResponseDto(Venta venta) {
        this.id = venta.getId();
        if (venta.getSucursal() != null) {
            this.sucursalId = venta.getSucursal().getId();
            this.sucursalNombre = venta.getSucursal().getNombre();
        }
        if (venta.getCliente() != null) {
            this.clienteId = venta.getCliente().getId();
            this.clienteNombre = venta.getCliente().getNombre();
        }
        this.subtotal = venta.getSubtotal();
        this.descuento = venta.getDescuento();
        this.total = venta.getTotal();
        this.estado = venta.getEstado();
        this.notes(venta.getNotas());
    }

    private void notes(String n) {
        this.notas = n;
    }
}
