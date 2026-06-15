package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {
    List<Empleado> findByEmpresaId(String empresaId);
}
