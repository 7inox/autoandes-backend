package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.Servicio; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
 public interface ServicioRepository extends JpaRepository<Servicio,Long> { boolean existsByCodigo(String codigo); boolean existsByCodigoAndIdNot(String codigo,Long id); }
