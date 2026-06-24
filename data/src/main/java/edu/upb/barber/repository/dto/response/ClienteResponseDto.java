package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDto {

    private String id;
    private String nombre;
    private String telefono;
    private String email;
    private String documento;
    private String notas;
    private boolean activo;

    @com.fasterxml.jackson.annotation.JsonProperty("empresa_id")
    private String empresaId;

    @com.fasterxml.jackson.annotation.JsonProperty("usuario_id")
    private String usuarioId;

    public ClienteResponseDto(Cliente cliente) {
        this.id = cliente.getId();
        this.nombre = cliente.getNombre();
        this.telefono = cliente.getTelefono();
        this.email = cliente.getEmail();
        this.documento = cliente.getDocumento();
        this.notas = cliente.getNotas();
        this.activo = cliente.isActivo();
        if (cliente.getEmpresa() != null) {
            this.empresaId = cliente.getEmpresa().getId();
        }
        if (cliente.getUsuario() != null) {
            this.usuarioId = cliente.getUsuario().getId();
        }
    }

    public ClienteResponseDto(ClienteRequestDto clienteRequestDto) {
    }
}