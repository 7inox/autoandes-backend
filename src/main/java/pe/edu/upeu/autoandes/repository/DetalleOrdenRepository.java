package pe.edu.upeu.autoandes.repository;
import pe.edu.upeu.autoandes.entity.DetalleOrden; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
 public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden,Long> { boolean existsByServicioId(Long id);
 @Query("select s.codigo as codigo,s.nombre as servicio,sum(d.cantidad) as cantidadTotal,sum(d.subtotal) as importeTotal from DetalleOrden d join d.servicio s join d.orden o where o.estado='REGISTRADA' and (:desde is null or o.fecha>=:desde) and (:hasta is null or o.fecha<:hasta) group by s.codigo,s.nombre order by sum(d.subtotal) desc")
 java.util.List<pe.edu.upeu.autoandes.dto.IngresoProjection> ingresos(@Param("desde") java.time.LocalDateTime desde,@Param("hasta") java.time.LocalDateTime hasta); }
