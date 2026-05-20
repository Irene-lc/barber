package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VentaResponse {
    private String id;
    private String sucursalId;
    private String sucursalNombre;
    private String clienteId;
    private String clienteNombre;
    private String agendaEventoId;
    private String registradoPorUsuarioId;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private EstadoVenta estado;
    private String notas;

    public static VentaResponse fromEntity(Venta v) {
        VentaResponse dto = new VentaResponse();
        dto.id = v.getId();
        if (v.getSucursal() != null) {
            dto.sucursalId = v.getSucursal().getId();
            dto.sucursalNombre = v.getSucursal().getNombre();
        }
        if (v.getCliente() != null) {
            dto.clienteId = v.getCliente().getId();
            dto.clienteNombre = v.getCliente().getNombre();
        }
        if (v.getAgendaEvento() != null) {
            dto.agendaEventoId = v.getAgendaEvento().getId();
        }
        if (v.getRegistradoPorUsuario() != null) {
            dto.registradoPorUsuarioId = v.getRegistradoPorUsuario().getId();
        }
        dto.subtotal = v.getSubtotal();
        dto.descuento = v.getDescuento();
        dto.total = v.getTotal();
        dto.estado = v.getEstado();
        dto.notas = v.getNotas();
        return dto;
    }
}
