package edu.upb.barber.service;

import ch.qos.logback.core.util.StringUtil;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.dto.request.UsuarioRequestDto;
import edu.upb.barber.repository.dto.response.UsuarioResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public void save(UsuarioRequestDto usuarioRequestDto) throws Exception {

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getNombre())) {
            log.error("Error al guardar usuario. El campo nombre es null");
            throw new Exception("El campo nombre es null");
        }

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getEmail())) {
            log.error("Error al guardar usuario. El campo email es null");
            throw new Exception("El campo email es null");
        }

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getPassword())) {
            log.error("Error al guardar usuario. El campo password es null");
            throw new Exception("El campo password es null");
        }

        Optional<Usuario> usuarioExistente =
                usuarioRepository.findByEmail(usuarioRequestDto.getEmail());

        if (usuarioExistente.isPresent()) {
            log.error("Ya existe un usuario con ese email");
            throw new Exception("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(usuarioRequestDto.getNombre());
        usuario.setApellido(usuarioRequestDto.getApellido());
        usuario.setEmail(usuarioRequestDto.getEmail());

        // luego puedes encriptarlo con BCrypt
        usuario.setPasswordHash(usuarioRequestDto.getPassword());

        usuario.setRol(usuarioRequestDto.getRol());

        if (usuarioRequestDto.getActivo() != null) {
            usuario.setActivo(usuarioRequestDto.getActivo());
        }

        if (usuarioRequestDto.getEmpresaId() != null) {

            Empresa empresa = empresaRepository
                    .findById(usuarioRequestDto.getEmpresaId())
                    .orElseThrow(() ->
                            new Exception("Empresa no encontrada"));

            usuario.setEmpresa(empresa);
        }

        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

}