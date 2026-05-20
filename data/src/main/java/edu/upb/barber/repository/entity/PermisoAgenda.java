package edu.upb.barber.repository.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.upb.barber.repository.entity.enums.TipoPermiso;
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
@Table(name = "permiso_agenda")
public class PermisoAgenda extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "otorgado_por_usuario_id")
    private Usuario otorgadoPorUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_permiso", nullable = false, length = 30)
    private TipoPermiso tipoPermiso;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta", nullable = false)
    private LocalDate fechaHasta;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    public String getId() { return id; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }
    public Usuario getOtorgadoPorUsuario() { return otorgadoPorUsuario; }
    public void setOtorgadoPorUsuario(Usuario otorgadoPorUsuario) { this.otorgadoPorUsuario = otorgadoPorUsuario; }
    public TipoPermiso getTipoPermiso() { return tipoPermiso; }
    public void setTipoPermiso(TipoPermiso tipoPermiso) { this.tipoPermiso = tipoPermiso; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
