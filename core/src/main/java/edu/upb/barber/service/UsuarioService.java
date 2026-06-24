package edu.upb.barber.service;

import ch.qos.logback.core.util.StringUtil;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.dto.request.UsuarioRequestDto;
import edu.upb.barber.repository.dto.response.UsuarioResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final LogService logService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(UsuarioRequestDto usuarioRequestDto) throws Exception {

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getNombre())) {
            log.error("Error al guardar usuario. El campo nombre es null");
            logService.error("Error al guardar usuario. El campo nombre es null");
            throw new OperationException("El campo nombre es null");
        }

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getEmail())) {
            log.error("Error al guardar usuario. El campo email es null");
            logService.error("Error al guardar usuario. El campo email es null");
            throw new OperationException("El campo email es null");
        }

        if (!isValidEmail(usuarioRequestDto.getEmail())) {
            log.error("Error al guardar usuario. Formato de email inválido: {}", usuarioRequestDto.getEmail());
            logService.error("Error al guardar usuario. Formato de email inválido: " + usuarioRequestDto.getEmail());
            throw new OperationException("El formato del correo electrónico es inválido");
        }

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getPassword())) {
            log.error("Error al guardar usuario. El campo password es null");
            logService.error("Error al guardar usuario. El campo password es null");
            throw new OperationException("El campo password es null");
        }

        Optional<Usuario> usuarioExistente =
                usuarioRepository.findByEmail(usuarioRequestDto.getEmail());

        if (usuarioExistente.isPresent()) {
            log.error("Ya existe un usuario con ese email");
            logService.error("Error al guardar usuario. Ya existe un usuario con ese email: " + usuarioRequestDto.getEmail());
            throw new OperationException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(usuarioRequestDto.getNombre());
        usuario.setApellido(usuarioRequestDto.getApellido());
        usuario.setTelefono(usuarioRequestDto.getTelefono());
        usuario.setDocumento(usuarioRequestDto.getDocumento());
        usuario.setEmail(usuarioRequestDto.getEmail());

        usuario.setPasswordHash(passwordEncoder.encode(usuarioRequestDto.getPassword()));

        usuario.setRol(usuarioRequestDto.getRol());

        if (usuarioRequestDto.getActivo() != null) {
            usuario.setActivo(usuarioRequestDto.getActivo());
        }

        if (usuarioRequestDto.getEmpresaId() != null) {

            Empresa empresa = empresaRepository
                    .findById(usuarioRequestDto.getEmpresaId())
                    .orElseThrow(() -> new OperationException("Empresa no encontrada"));

            usuario.setEmpresa(empresa);
        }

        usuarioRepository.save(usuario);
        logService.info("Usuario guardado exitosamente: " + usuarioRequestDto.getEmail());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDto::new)
                .toList();
    }

    @Cacheable(value = "usuario", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    @Async
    @CacheEvict(value = "usuario", allEntries = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(String usuarioId, UsuarioRequestDto usuarioRequestDto) throws Exception {

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getNombre())) {
            log.error("Error al actualizar usuario. El campo nombre es null");
            logService.error("Error al actualizar usuario. El campo nombre es null");
            throw new OperationException("El campo nombre es null");
        }

        if (StringUtil.isNullOrEmpty(usuarioRequestDto.getEmail())) {
            log.error("Error al actualizar usuario. El campo email es null");
            logService.error("Error al actualizar usuario. El campo email es null");
            throw new OperationException("El campo email es null");
        }

        if (!isValidEmail(usuarioRequestDto.getEmail())) {
            log.error("Error al actualizar usuario. Formato de email inválido: {}", usuarioRequestDto.getEmail());
            logService.error("Error al actualizar usuario. Formato de email inválido: " + usuarioRequestDto.getEmail());
            throw new OperationException("El formato del correo electrónico es inválido");
        }

        Optional<Usuario> optionalUsuario = this.usuarioRepository.findById(usuarioId);
        if (optionalUsuario.isEmpty()) {
            logService.error("Error al actualizar usuario. No existe el usuario con id: " + usuarioId);
            throw new OperationException("No existe el usuario con el id: " + usuarioId);
        }

        Usuario usuario = optionalUsuario.get();

        usuario.setNombre(usuarioRequestDto.getNombre());
        usuario.setApellido(usuarioRequestDto.getApellido());
        usuario.setTelefono(usuarioRequestDto.getTelefono());
        usuario.setDocumento(usuarioRequestDto.getDocumento());
        if (!StringUtil.isNullOrEmpty(usuarioRequestDto.getPassword())) {
            usuario.setPasswordHash(passwordEncoder.encode(usuarioRequestDto.getPassword()));
        }
        usuario.setEmail(usuarioRequestDto.getEmail());
        usuario.setRol(usuarioRequestDto.getRol());
        if (usuarioRequestDto.getActivo() != null) {
            usuario.setActivo(usuarioRequestDto.getActivo());
        }
        if (usuarioRequestDto.getEmpresaId() != null) {
            Empresa empresa = empresaRepository
                    .findById(usuarioRequestDto.getEmpresaId())
                    .orElseThrow(() -> new OperationException("Empresa no encontrada"));
            usuario.setEmpresa(empresa);
        }

        usuarioRepository.save(usuario);
        logService.info("Usuario actualizado exitosamente: " + usuarioId);
    }

    @Cacheable(value = "usuario", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUserIdToValidateSession(String id) {
        return usuarioRepository.findByUserIdToValidateSession(id);
    }

    @Cacheable(value = "usuario", key = "#username")
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) {
        Optional<Usuario> byEmail = usuarioRepository.findByEmail(username);
        if (byEmail.isPresent()) {
            return byEmail;
        }
        return usuarioRepository.findByNombreIgnoreCase(username);
    }

    @Transactional
    public void delete(String usuarioId) throws Exception {
        if (!usuarioRepository.existsById(usuarioId)) {
            log.error("Error al eliminar usuario. No encontrado con id: {}", usuarioId);
            logService.error("Error al eliminar usuario. No encontrado con id: " + usuarioId);
            throw new OperationException("Usuario no encontrado con id: " + usuarioId);
        }
        usuarioRepository.deleteById(usuarioId);
        logService.info("Usuario eliminado exitosamente: " + usuarioId);
    }

    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

}
