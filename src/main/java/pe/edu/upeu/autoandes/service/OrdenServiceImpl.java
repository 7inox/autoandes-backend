package pe.edu.upeu.autoandes.service;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j; import java.util.*; import java.time.*; import java.math.BigDecimal; import jakarta.persistence.EntityManager; import pe.edu.upeu.autoandes.entity.*; import pe.edu.upeu.autoandes.dto.*; import pe.edu.upeu.autoandes.repository.*; import pe.edu.upeu.autoandes.mapper.AutoAndesMapper; import pe.edu.upeu.autoandes.exception.DomainException;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true) public class OrdenServiceImpl implements OrdenService {
 private final VehiculoRepository vehiculos; private final ServicioRepository servicios; private final OrdenServicioRepository ordenes; private final DetalleOrdenRepository detalles; private final AutoAndesMapper mapper; private final EntityManager em; private final Clock clock;
 @Transactional public OrdenResponseDTO registrar(OrdenRequestDTO d,String username){
  var vehiculo=vehiculos.lockById(d.vehiculoId()).orElseThrow(()->DomainException.missing("Vehículo"));
  var ultima=ordenes.findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(d.vehiculoId(),"REGISTRADA");
  if(ultima.isPresent()&&d.kilometraje()<ultima.get().getKilometraje())throw DomainException.conflict("El kilometraje no puede ser inferior al último registrado: "+ultima.get().getKilometraje());
  if(d.detalles().stream().map(DetalleRequestDTO::servicioId).distinct().count()!=d.detalles().size())throw new DomainException(400,"No se permite repetir servicios");
  var orden=new OrdenServicio(); orden.setVehiculo(vehiculo);orden.setFecha(LocalDateTime.now(clock));orden.setEstado("REGISTRADA");orden.setKilometraje(d.kilometraje());orden.setUsuarioRegistro(username);orden.setTotal(BigDecimal.ZERO);
  // Oracle sequences are not transactional: gaps after rollback are expected; numbers remain unique.
  Number n=(Number)em.createNativeQuery("SELECT SEQ_ORDEN_NUMERO.NEXTVAL FROM DUAL",Number.class).getSingleResult();
  orden.setNumero("OS-%06d".formatted(n.longValue()));ordenes.saveAndFlush(orden);
  for(var linea:d.detalles()){
   var servicio=servicios.findById(linea.servicioId()).orElseThrow(()->DomainException.missing("Servicio"));
   if(!Boolean.TRUE.equals(servicio.getActivo()))throw DomainException.conflict("El servicio "+servicio.getCodigo()+" está inactivo");
   var detalle=new DetalleOrden();detalle.setOrden(orden);detalle.setServicio(servicio);detalle.setCantidad(linea.cantidad());detalle.setPrecioUnitario(servicio.getPrecio());detalle.setSubtotal(servicio.getPrecio().multiply(BigDecimal.valueOf(linea.cantidad())));orden.getDetalles().add(detalle);orden.setTotal(orden.getTotal().add(detalle.getSubtotal()));
   // Flush each line so an invalid third service proves rollback of already inserted rows.
   em.persist(detalle);em.flush();
  }
  ordenes.flush();log.info("Orden {} registrada: {} detalles, total {}",orden.getNumero(),orden.getDetalles().size(),orden.getTotal());return mapper.orden(orden);
 }
 @Transactional public OrdenResponseDTO anular(Long id,String motivo){var o=ordenes.lockById(id).orElseThrow(()->DomainException.missing("Orden"));if(!"REGISTRADA".equals(o.getEstado()))throw DomainException.conflict("Solo se anula una orden REGISTRADA");if(!o.getFecha().toLocalDate().equals(LocalDate.now(clock)))throw DomainException.conflict("La orden solo puede anularse el mismo día de su registro");o.setEstado("ANULADA");o.setMotivoAnulacion(motivo);ordenes.flush();log.info("Orden {} anulada",o.getNumero());return mapper.orden(o);}
 public OrdenResponseDTO obtener(Long id){return mapper.orden(ordenes.findById(id).orElseThrow(()->DomainException.missing("Orden")));}
 private void fechas(LocalDate desde,LocalDate hasta){if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw new DomainException(400,"La fecha desde no puede superar hasta");}
 public List<OrdenResponseDTO> listar(String placa,String estado,LocalDate desde,LocalDate hasta){fechas(desde,hasta);if(estado!=null&&!Set.of("REGISTRADA","ANULADA").contains(estado))throw new DomainException(400,"Estado inválido");return ordenes.filtrar(placa,estado,desde==null?null:desde.atStartOfDay(),hasta==null?null:hasta.plusDays(1).atStartOfDay()).stream().map(mapper::orden).toList();}
 public List<IngresoProjection> ingresos(LocalDate desde,LocalDate hasta){fechas(desde,hasta);return detalles.ingresos(desde==null?null:desde.atStartOfDay(),hasta==null?null:hasta.plusDays(1).atStartOfDay());}
}
