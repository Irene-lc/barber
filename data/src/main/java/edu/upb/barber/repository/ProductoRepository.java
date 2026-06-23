package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findByEmpresa(Empresa empresa);
    List<Producto> findByEmpresaId(String empresaId);
}
