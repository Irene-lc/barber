package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findByEmpresa(Empresa empresa);
    List<Producto> findByEmpresaId(String empresaId);

    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR :nombre = '' OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:empresaId IS NULL OR :empresaId = '' OR p.empresa.id = :empresaId) " +
           "AND (:activo IS NULL OR p.activo = :activo)")
    Page<Producto> findByFilters(
            @Param("nombre") String nombre,
            @Param("empresaId") String empresaId,
            @Param("activo") Boolean activo,
            Pageable pageable);
}
