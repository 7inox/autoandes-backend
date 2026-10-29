package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.Rol; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
 public interface RolRepository extends JpaRepository<Rol,Long> {  }
