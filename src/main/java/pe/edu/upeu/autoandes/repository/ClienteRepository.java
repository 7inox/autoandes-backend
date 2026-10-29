package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.Cliente; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
 public interface ClienteRepository extends JpaRepository<Cliente,Long> {  }
