package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.Vehiculo; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
 public interface VehiculoRepository extends JpaRepository<Vehiculo,Long> { boolean existsByPlaca(String placa); boolean existsByPlacaAndIdNot(String placa,Long id);
 @EntityGraph(attributePaths="cliente") java.util.List<Vehiculo> findByClienteId(Long id);
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select v from Vehiculo v where v.id=:id") java.util.Optional<Vehiculo> lockById(@Param("id") Long id); }
