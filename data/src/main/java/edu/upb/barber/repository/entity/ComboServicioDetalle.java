package edu.upb.barber.repository.entity;

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
    name = "combo_servicio_detalle",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_combo_servicio", columnNames = {"combo_servicio_id", "servicio_id"})
    }
)
public class ComboServicioDetalle extends BaseAuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "combo_servicio_id", nullable = false)
    private ComboServicio comboServicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(name = "orden_ejecucion", nullable = false)
    private int ordenEjecucion = 1;

    public String getId() { return id; }
    public ComboServicio getComboServicio() { return comboServicio; }
    public void setComboServicio(ComboServicio comboServicio) { this.comboServicio = comboServicio; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public int getOrdenEjecucion() { return ordenEjecucion; }
    public void setOrdenEjecucion(int ordenEjecucion) { this.ordenEjecucion = ordenEjecucion; }
}
