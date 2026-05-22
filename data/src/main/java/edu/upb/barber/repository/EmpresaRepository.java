package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.response.EmpresaDto;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, String> {
    @Query("SELECT e FROM Empresa  e")
    List<EmpresaDto> listarEmpresas();

    List<Empresa> findByNombre(String nombre);

    @Query("SELECT e FROM Empresa e WHERE e.email=:pNombre")
    List<EmpresaDto> findByNombreAux(@Param("pNombre") String nombre);

    @Query("SELECT new edu.upb.barber.repository.dto.response.EmpresaDto(e.id, e.email)  " +
            "FROM Empresa e WHERE e.email=:pNombre")
    List<EmpresaDto> findByNombreAuxB(@Param("pNombre") String nombre);

    @Query("SELECT new edu.upb.barber.repository.dto.response.EmpresaDto(e.email)  " +
            "FROM Empresa e WHERE e.email=:pNombre")
    List<EmpresaDto> findByNombreAuxC(@Param("pNombre") String nombre);

    /*
     private Boolean activa;
    private String nit;
    private String nombre;
    private String razon_social;
     */
    @Modifying
    @Query("UPDATE Empresa e SET e.nombre=:pNombre, e.nit=:pNit, e.razonSocial=:pRazonSocial")
    void actualizarEmpresa(
            @Param("pEmpresaId")String pEmpresaId,
            @Param("pNombre")String nombre,
            @Param("pNit") String nit,
            @Param("pRazonSocial") String razonSocial);
}
