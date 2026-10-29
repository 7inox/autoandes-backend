package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_orden")
@Getter
@Setter
@NoArgsConstructor
public class DetalleOrden {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "orden_id")
  private OrdenServicio orden;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "servicio_id")
  private Servicio servicio;

  @Column(nullable = false, precision = 2)
  private Integer cantidad;

  @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
  private java.math.BigDecimal precioUnitario;

  @Column(nullable = false, precision = 10, scale = 2)
  private java.math.BigDecimal subtotal;
}
