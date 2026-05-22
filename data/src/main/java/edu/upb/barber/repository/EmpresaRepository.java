package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, String> {

    Optional<Empresa> findByNit(String nit);

    Optional<Empresa> findByEmail(String email);

    List<Empresa> findByNombre(String nombre);

    @Query("""
            SELECT new edu.upb.barber.repository.dto.response.EmpresaResponseDto(
                e.id,
                e.nombre,
                e.nit
            )
            FROM Empresa e
            WHERE e.nombre = :pNombre
            """)
    List<EmpresaResponseDto> findByNombreAux(
            @Param("pNombre") String nombre
    );
    @Modifying
    @Query("UPDATE Empresa e SET e.nombre=:pNombre, e.nit=:pNit, e.razonSocial=:pRazonSocial")
    void actualizarEmpresa(
            @Param("pEmpresaId")String pEmpresaId,
            @Param("pNombre")String nombre,
            @Param("pNit") String nit,
            @Param("pRazonSocial") String razonSocial);

}