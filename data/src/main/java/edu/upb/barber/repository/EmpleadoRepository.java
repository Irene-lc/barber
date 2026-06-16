package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {
    List<Empleado> findByEmpresaId(String empresaId);
    Optional<Empleado> findByUsuario_Email(String email);

}
