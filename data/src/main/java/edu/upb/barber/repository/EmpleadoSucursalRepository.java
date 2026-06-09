package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.EmpleadoSucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoSucursalRepository extends JpaRepository<EmpleadoSucursal, String> {

    boolean existsByEmpleadoIdAndSucursalIdAndActivoTrue(String empleadoId, String sucursalId);

    Optional<EmpleadoSucursal> findByEmpleadoIdAndSucursalId(String empleadoId, String sucursalId);
}
