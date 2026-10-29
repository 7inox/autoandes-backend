package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(name = "nombrecompleto", nullable = false)
  private String nombreCompleto;

  @Convert(converter = BooleanNumberConverter.class)
  @Column(nullable = false, columnDefinition = "NUMBER(1)")
  private Boolean activo;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "usuario_roles",
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "rol_id"))
  private java.util.Set<Rol> roles = new java.util.HashSet<>();
}
