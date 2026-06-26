package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, String> {
    java.util.List<VentaDetalle> findByVentaId(String ventaId);
    java.util.List<VentaDetalle> findByVentaIdIn(java.util.List<String> ventaIds);
}
