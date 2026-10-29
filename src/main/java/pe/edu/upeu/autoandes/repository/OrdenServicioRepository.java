package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.autoandes.entity.OrdenServicio;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {
  boolean existsByVehiculoId(Long id);

  java.util.Optional<OrdenServicio> findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(
      Long id, String estado);

  @Query(
      "select o from OrdenServicio o join o.vehiculo v where (:placa is null or upper(v.placa) like"
          + " upper(concat('%',:placa,'%'))) and (:estado is null or o.estado=:estado) and (:desde"
          + " is null or o.fecha>=:desde) and (:hasta is null or o.fecha<:hasta) order by o.fecha"
          + " desc,o.id desc")
  java.util.List<OrdenServicio> filtrar(
      @Param("placa") String placa,
      @Param("estado") String estado,
      @Param("desde") java.time.LocalDateTime desde,
      @Param("hasta") java.time.LocalDateTime hasta);

  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select o from OrdenServicio o where o.id=:id")
  java.util.Optional<OrdenServicio> lockById(@Param("id") Long id);
}
