package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.*;
import pe.edu.upeu.autoandes.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  @EntityGraph(attributePaths = "roles")
  java.util.Optional<Usuario> findByUsername(String username);
}
