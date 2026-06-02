package edu.upb.barber.repository.dto.response;

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

    public ClienteResponseDto(Cliente cliente) {
        this.id = cliente.getId();
        this.nombre = cliente.getNombre();
        this.telefono = cliente.getTelefono();
        this.email = cliente.getEmail();
        this.documento = cliente.getDocumento();
        this.notas = cliente.getNotas();
        this.activo = cliente.isActivo();
    }
}