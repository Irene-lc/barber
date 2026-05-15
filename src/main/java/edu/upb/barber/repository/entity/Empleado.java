package edu.upb.barber.repository.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "empleado",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_empleado_usuario", columnNames = "usuario_id")
    }
)
public class Empleado extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "cargo", length = 80)
    private String cargo;

    @Column(name = "especialidad", length = 120)
    private String especialidad;

    @Column(name = "porcentaje_comision", precision = 5, scale = 2)
    private BigDecimal porcentajeComision;

    @Column(name = "pago_fijo_mensual", precision = 12, scale = 2)
    private BigDecimal pagoFijoMensual;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public String getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public BigDecimal getPorcentajeComision() { return porcentajeComision; }
    public void setPorcentajeComision(BigDecimal porcentajeComision) { this.porcentajeComision = porcentajeComision; }
    public BigDecimal getPagoFijoMensual() { return pagoFijoMensual; }
    public void setPagoFijoMensual(BigDecimal pagoFijoMensual) { this.pagoFijoMensual = pagoFijoMensual; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
