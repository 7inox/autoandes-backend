package pe.edu.upeu.autoandes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.config.JwtService;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.exception.DomainException;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;
import pe.edu.upeu.autoandes.security.PermisosPorRol;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
  private final UsuarioRepository usuarios;
  private final BCryptPasswordEncoder encoder;
  private final JwtService jwt;

  public LoginResponseDTO login(LoginRequestDTO d) {
    var u = usuarios.findByUsername(d.username()).orElse(null);
    if (u == null
        || !encoder.matches(d.password(), u.getPassword())
        || !Boolean.TRUE.equals(u.getActivo()))
      throw new DomainException(401, "Credenciales inválidas");
    var me = datos(u);
    return new LoginResponseDTO(
        jwt.emitir(u.getUsername()),
        "Bearer",
        3600,
        me.nombreCompleto(),
        me.roles(),
        me.permisos());
  }

  private UsuarioResponseDTO datos(Usuario u) {
    var roles =
        u.getRoles().stream()
            .map(r -> r.getNombre())
            .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
    return new UsuarioResponseDTO(
        u.getUsername(), u.getNombreCompleto(), roles, PermisosPorRol.permisos(roles));
  }

  public UsuarioResponseDTO me(String username) {
    return datos(
        usuarios
            .findByUsername(username)
            .orElseThrow(() -> new DomainException(401, "Credenciales inválidas")));
  }
}
