package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Comision;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class ComisionResponse {
    private String id;
    private String ventaDetalleId;
    private String empleadoId;
    private String empleadoNombre;
    private BigDecimal porcentajeAplicado;
    private BigDecimal montoComision;
    private boolean liquidada;
    private OffsetDateTime liquidadaEn;

    public static ComisionResponse fromEntity(Comision c) {
        ComisionResponse dto = new ComisionResponse();
        dto.id = c.getId();
        if (c.getVentaDetalle() != null) {
            dto.ventaDetalleId = c.getVentaDetalle().getId();
        }
        if (c.getEmpleado() != null) {
            dto.empleadoId = c.getEmpleado().getId();
            dto.empleadoNombre = c.getEmpleado().getNombre();
        }
        dto.porcentajeAplicado = c.getPorcentajeAplicado();
        dto.montoComision = c.getMontoComision();
        dto.liquidada = c.isLiquidada();
        dto.liquidadaEn = c.getLiquidadaEn();
        return dto;
    }
}
