package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, String> {
    List<Sucursal> findByEmpresaId(String empresaId);
    List<Sucursal> findByEmpresa(Empresa empresa);
}
