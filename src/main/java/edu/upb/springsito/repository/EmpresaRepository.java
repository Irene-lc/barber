package edu.upb.springsito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.upb.springsito.repository.entity.Empresa;
import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, String> {
    @Query("SELECT e FROM Empresa e where e.nombre='Mi Empresa'")
     List<Empresa> listar();

    @Query("SELECT e FROM Empresa e where e.nombre = :nombre")
     Empresa buscarPorNombreEmpresa(@Param("nombre") String nombre);


}

