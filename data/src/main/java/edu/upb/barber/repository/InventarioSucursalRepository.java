package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.InventarioSucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioSucursalRepository extends JpaRepository<InventarioSucursal, String> {
    java.util.Optional<InventarioSucursal> findByProductoIdAndSucursalId(String productoId, String sucursalId);
    java.util.List<InventarioSucursal> findBySucursalId(String sucursalId);
    java.util.List<InventarioSucursal> findByProductoIdInAndSucursalId(java.util.List<String> productoIds, String sucursalId);
}
