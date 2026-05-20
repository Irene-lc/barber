package edu.upb.barber.repository.entity;

import java.math.BigDecimal;

import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "venta_detalle")
public class VentaDetalle extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 30)
    private TipoItemVenta tipoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_servicio_id")
    private ComboServicio comboServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @Column(name = "cantidad", nullable = false)
    private int cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    public String getId() { return id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public TipoItemVenta getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItemVenta tipoItem) { this.tipoItem = tipoItem; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public ComboServicio getComboServicio() { return comboServicio; }
    public void setComboServicio(ComboServicio comboServicio) { this.comboServicio = comboServicio; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
