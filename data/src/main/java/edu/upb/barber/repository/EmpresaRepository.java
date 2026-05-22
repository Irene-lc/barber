package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, String> {
    @Query("SELECT e FROM Empresa  e WHERE e.nombre='Empresa 1' ")
    List<Empresa> listarEmpresas();

    List<Empresa> findByNombre(String nombre);

    @Query("SELECT e FROM Empresa e WHERE e.nombre=:pNombre")
    List<EmpresaResponseDto> findByNombreAux(@Param("pNombre") String nombre);

    @Query("SELECT new edu.upb.barber.repository.dto.response.EmpresaResponseDto(e.id, e.nombre)  " +
            "FROM Empresa e WHERE e.nombre=:pNombre")
    List<EmpresaResponseDto> findByNombreAuxB(@Param("pNombre") String nombre);

    @Query("SELECT new edu.upb.barber.repository.dto.response.EmpresaResponseDto(e.nombre)  " +
            "FROM Empresa e WHERE e.nombre=:pNombre")
    List<EmpresaResponseDto> findByNombreAuxC(@Param("pNombre") String nombre);

}
