package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.InventarioSucursal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventarioSucursalResponseDto {

    private String id;

    @JsonProperty("producto_id")
    private String productoId;

    @JsonProperty("producto_nombre")
    private String productoNombre;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    @JsonProperty("stock_actual")
    private int stockActual;

    @JsonProperty("stock_minimo")
    private int stockMinimo;

    private Boolean activo;

    public java.lang.String getId() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getProductoId() {
        return productoId;
    }

    public void setProductoId(java.lang.String productoId) {
        this.productoId = productoId;
    }

    public java.lang.String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(java.lang.String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public java.lang.String getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(java.lang.String sucursalId) {
        this.sucursalId = sucursalId;
    }

    public java.lang.String getSucursalNombre() {
        return sucursalNombre;
    }

    public void setSucursalNombre(java.lang.String sucursalNombre) {
        this.sucursalNombre = sucursalNombre;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public java.lang.Boolean getActivo() {
        return activo;
    }

    public void setActivo(java.lang.Boolean activo) {
        this.activo = activo;
    }

    public InventarioSucursalResponseDto(InventarioSucursal inventario) {
        this.id = inventario.getId();
        if (inventario.getProducto() != null) {
            this.productoId = inventario.getProducto().getId();
            this.productoNombre = inventario.getProducto().getNombre();
        }
        if (inventario.getSucursal() != null) {
            this.sucursalId = inventario.getSucursal().getId();
            this.sucursalNombre = inventario.getSucursal().getNombre();
        }
        this.stockActual = inventario.getStockActual();
        this.stockMinimo = inventario.getStockMinimo();
        this.activo = inventario.isActivo();
    }
}
