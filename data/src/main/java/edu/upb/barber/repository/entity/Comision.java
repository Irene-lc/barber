package edu.upb.barber.repository.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "comision",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_comision_venta_detalle_empleado", columnNames = {"venta_detalle_id", "empleado_id"})
    }
)
public class Comision extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_detalle_id", nullable = false)
    private VentaDetalle ventaDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "porcentaje_aplicado", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAplicado;

    @Column(name = "monto_comision", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoComision;

    @Column(name = "liquidada", nullable = false)
    private boolean liquidada = false;

    @Column(name = "liquidada_en")
    private OffsetDateTime liquidadaEn;

    public String getId() { return id; }
    public VentaDetalle getVentaDetalle() { return ventaDetalle; }
    public void setVentaDetalle(VentaDetalle ventaDetalle) { this.ventaDetalle = ventaDetalle; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public BigDecimal getPorcentajeAplicado() { return porcentajeAplicado; }
    public void setPorcentajeAplicado(BigDecimal porcentajeAplicado) { this.porcentajeAplicado = porcentajeAplicado; }
    public BigDecimal getMontoComision() { return montoComision; }
    public void setMontoComision(BigDecimal montoComision) { this.montoComision = montoComision; }
    public boolean isLiquidada() { return liquidada; }
    public void setLiquidada(boolean liquidada) { this.liquidada = liquidada; }
    public OffsetDateTime getLiquidadaEn() { return liquidadaEn; }
    public void setLiquidadaEn(OffsetDateTime liquidadaEn) { this.liquidadaEn = liquidadaEn; }
}
