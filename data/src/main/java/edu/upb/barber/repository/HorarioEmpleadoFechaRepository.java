package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.HorarioEmpleadoFecha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HorarioEmpleadoFechaRepository extends JpaRepository<HorarioEmpleadoFecha, String> {
    List<HorarioEmpleadoFecha> findByEmpleadoIdAndSucursalIdAndFechaAndActivoTrue(String empleadoId, String sucursalId, LocalDate fecha);
    List<HorarioEmpleadoFecha> findByEmpleadoIdAndSucursalIdAndFecha(String empleadoId, String sucursalId, LocalDate fecha);
    List<HorarioEmpleadoFecha> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
