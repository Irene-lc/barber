package edu.upb.barber.repository;

import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.dto.response.ClienteResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {


    @Query("SELECT c FROM Cliente c WHERE c.createdDate BETWEEN :pInit AND :pEnd")
    Page<Cliente> findAllByOderByDataDesc(
            @Param("pInit") LocalDateTime pInit,
            @Param("pEnd") LocalDateTime pEnd,
            Pageable pageable);

    Optional<Cliente> findByEmailAndEmpresa(String email, Empresa empresa);

    List<Cliente> findByUsuarioId(String usuarioId);

    Optional<Cliente> findByUsuarioIdAndEmpresaId(String usuarioId, String empresaId);

}
