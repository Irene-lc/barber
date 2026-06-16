package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, String> {
    List<Servicio> findByEmpresaId(String empresaId);
    List<Servicio> findByEmpresa(Empresa empresa);
    Optional<Servicio> findByEmpresaAndNombre(Empresa empresa, String nombre);
}
