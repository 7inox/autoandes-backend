package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
public class Servicio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 7)
  private String codigo;

  @Column(nullable = false, length = 80)
  private String nombre;

  @Column(precision = 10, scale = 2, nullable = false)
  private java.math.BigDecimal precio;

  @Column(name = "duracion_min", nullable = false)
  private Integer duracionMin;

  @Convert(converter = BooleanNumberConverter.class)
  @Column(nullable = false, columnDefinition = "NUMBER(1)")
  private Boolean activo = true;
}
